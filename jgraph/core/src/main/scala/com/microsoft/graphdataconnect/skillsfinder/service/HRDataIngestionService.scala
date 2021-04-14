package com.microsoft.graphdataconnect.skillsfinder.service

import java.time.{ZoneOffset, ZonedDateTime}

import com.microsoft.graphdataconnect.model.admin.HRDataIngestionPhase
import com.microsoft.graphdataconnect.model.admin.HRDataIngestionPhase.HRDataIngestionPhase
import com.microsoft.graphdataconnect.skillsfinder.config.MdcUtil
import com.microsoft.graphdataconnect.skillsfinder.exceptions._
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.{HRDataIngestionStateModel, HRDataIngestionStateResponse, UserToken}
import com.microsoft.graphdataconnect.skillsfinder.service.adf.ADFService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.{Isolation, Transactional}
import org.springframework.web.multipart.MultipartFile

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

@Service
class HRDataIngestionService(@Autowired private val hrUploadService: HRDataFileUploadService,
                             @Autowired private val adfService: ADFService,
                             @Autowired implicit val executionContext: ExecutionContext,
                             @Autowired val webSocketService: WebSocketService,
                             @Autowired val hrDataIngestionStateService: HRDataIngestionStateService) {

  @Value("${adf.polling.attempts.pipelines}")
  var adfPipelinesPollingAttempts: Int = _
  @Value("${adf.polling.interval}")
  var adfPollingInterval: Int = _
  @Value("${adf.polling.attempts.cleanup}")
  var adfCleanupPollingAttempts: Int = _

  private val logger: Logger = LoggerFactory.getLogger(classOf[HRDataIngestionService])

  @Transactional(isolation = Isolation.SERIALIZABLE)
  def startHRDataIngestion(userId: String, multipartFile: MultipartFile, storageUserToken: UserToken, adfUserToken: UserToken): Unit = {
    val currentHRDataIngestionStateOpt: Option[HRDataIngestionStateModel] = hrDataIngestionStateService.getHRDataIngestionState()

    val currentHRDataIngestionState = currentHRDataIngestionStateOpt.getOrElse(HRDataIngestionStateModel.default)

    if (currentHRDataIngestionState.phase != HRDataIngestionPhase.Error && currentHRDataIngestionState.phase != HRDataIngestionPhase.EmployeePipelineRunFinished) {
      throw new HRDataIngestionRejectedException("HR data ingestion is already in progress")
    }

    val newHRDataIngestionState = HRDataIngestionStateModel(phase = HRDataIngestionPhase.StartingFileUpload,
      requester = userId,
      startTime = ZonedDateTime.now(ZoneOffset.UTC),
      errorMessage = "",
      errorStackTrace = "",
      logsCorrelationId = "")

    changeHRDataIngestionState(newHRDataIngestionState)

    startAsyncHRDataIngestion(multipartFile, initialHRDataIngestionState = newHRDataIngestionState, storageUserToken = storageUserToken, adfUserToken = adfUserToken)
  }

  private def startAsyncHRDataIngestion(multipartFile: MultipartFile,
                                        initialHRDataIngestionState: HRDataIngestionStateModel,
                                        storageUserToken: UserToken,
                                        adfUserToken: UserToken): Unit = {
    // Since the Future is executed in another thread (other that the one in which the current HTTP request is processed in)
    // we need to explicitly pass the current correlationId (which is thread specific) to that thread
    val correlationId = MdcUtil.getCorrelationId()
    Future {
      ingestHRDataFromFile(multipartFile, initialHRDataIngestionState, correlationId, storageUserToken, adfUserToken)
    }
  }

  private def ingestHRDataFromFile(multipartFile: MultipartFile,
                                   initialHRDataIngestionState: HRDataIngestionStateModel,
                                   correlationId: String,
                                   storageUserToken: UserToken,
                                   adfUserToken: UserToken): Unit = {
    try {
      MdcUtil.setCorrelationId(correlationId)

      logger.info("Started the HR data ingestion process in the background")

      hrUploadService.writeHrData(multipartFile, storageUserToken)

      changeHRDataIngestionState(HRDataIngestionPhase.StartingEmployeePipelineRun)

      val end2endEmployeePipelineRunId: Option[String] = adfService.startEnd2EndEmployeeProfilePipeline()(adfUserToken)
      if (end2endEmployeePipelineRunId.isEmpty) {
        throw new PipelineRunCreationFailedException(ADFService.END2END_EMPLOYEE_PIPELINE_NAME)
      }
      logger.info(s"Waiting for completion of pipeline ${ADFService.END2END_EMPLOYEE_PIPELINE_NAME} with run id ${end2endEmployeePipelineRunId.get}. " +
        s"Polling its state every $adfPollingInterval seconds, for at most $adfPipelinesPollingAttempts times.")
      waitForPipeline(pipelineName = ADFService.END2END_EMPLOYEE_PIPELINE_NAME, pipelineRunId = end2endEmployeePipelineRunId.get)(adfUserToken)

      val importHrDataIntoSqlPipelineRunIdOpt = adfService.getImportHRDataIntoSqlPipelineLatestRunIdInLastMonth()(adfUserToken)
      if (importHrDataIntoSqlPipelineRunIdOpt.isEmpty) {
        throw new PipelineRunCreationFailedException(ADFService.IMPORT_HR_DATA_INTO_SQL_PIPELINE_NAME)
      }

      val importHrDataIntoSqlPipelineRunId = importHrDataIntoSqlPipelineRunIdOpt.get
      logger.info(s"Checking that ${ADFService.IMPORT_HR_DATA_INTO_SQL_PIPELINE_NAME} pipeline run with id $importHrDataIntoSqlPipelineRunId finished successfully.")
      if (!adfService.isPipelineInSucceededStatus(importHrDataIntoSqlPipelineRunId)(adfUserToken)) {
        throw new ADFPipelineRunFailed(message = s"${ADFService.IMPORT_HR_DATA_INTO_SQL_PIPELINE_NAME} pipeline run with id $importHrDataIntoSqlPipelineRunId failed!", runId = importHrDataIntoSqlPipelineRunId)
      }

      changeHRDataIngestionState(HRDataIngestionPhase.EmployeePipelineRunFinished)
    } catch {
      case e: Throwable =>
        logger.error("Failed to ingest HR Data!", e)
        val errorState = initialHRDataIngestionState.copy(
          phase = HRDataIngestionPhase.Error,
          errorMessage = e.getMessage,
          errorStackTrace = ExceptionUtils.getStackTrace(e),
          logsCorrelationId = MdcUtil.getCorrelationId()
        )
        changeHRDataIngestionState(errorState)
    } finally {
      MdcUtil.resetCorrelationId()
    }
  }

  @tailrec
  private def waitForPipeline(pipelineName: String, pipelineRunId: String, attemptsLeft: Int = adfPipelinesPollingAttempts)(implicit userToken: UserToken): Unit = {
    val isPipelineRunInProgress = adfService.isPipelineRunInProgress(pipelineRunId)
    if (isPipelineRunInProgress && attemptsLeft > 0) {
      logger.trace(s"Waiting $adfPollingInterval seconds before checking again the status of pipeline $pipelineName with run id $pipelineRunId. Attempts left: $attemptsLeft")
      Thread.sleep(adfPollingInterval * 1000)
      waitForPipeline(pipelineName, pipelineRunId, attemptsLeft - 1)
    } else if (adfService.isPipelineRunInFailedStatus(pipelineRunId)) {
      throw new ADFPipelineRunFailed(message = s"$pipelineName run with id $pipelineRunId failed!", runId = pipelineRunId)
    }
    else if (attemptsLeft == 0) {
      logger.error(s"The number of attempts left is now 0, can't wait anymore for $pipelineName to finish. Regarding the HR Data ingestion as failed")
      throw new ExceededWaitingTimeException(s"Exceeded waiting time for pipeline $pipelineName with run id $pipelineRunId")
    }
  }

  private def changeHRDataIngestionState(hrDataIngestionPhase: HRDataIngestionPhase): Unit = {
    val currentHRDataIngestionStateOpt: Option[HRDataIngestionStateModel] = hrDataIngestionStateService.getHRDataIngestionState()
    if (currentHRDataIngestionStateOpt.isEmpty) {
      throw new ServerErrorException("There is no HR Data ingestion phase stored in the database!")
    }
    val currentHRDataIngestionState = currentHRDataIngestionStateOpt.get
    changeHRDataIngestionState(currentHRDataIngestionState.copy(hrDataIngestionPhase))
  }

  private def changeHRDataIngestionState(hrDataIngestionStateModel: HRDataIngestionStateModel): Unit = {
    hrDataIngestionStateService.saveHRDataIngestionState(hrDataIngestionStateModel)
    webSocketService.emitNotification(WebSocketService.INGEST_HR_DATA_STATE_CHANNEL_PATH, HRDataIngestionStateResponse(hrDataIngestionStateModel.phase))
    logger.info(s"Changed HR data ingestion state to ${hrDataIngestionStateModel.phase} ")
  }

}
