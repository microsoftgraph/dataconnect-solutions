/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service.adf

import java.time.format.DateTimeFormatter
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Service
class ADFService(@Autowired val adfRestClient: AdfRestClient) {

  @Value("${adf.triggers.emailsBackfillTimeSpan}")
  var emailsBackfillTimeSpan: Int = _

  private val triggersDateTimeFormatter = DateTimeFormatter.ISO_INSTANT

  private val logger: Logger = LoggerFactory.getLogger(classOf[ADFService])

  def getCleanupPipelineLatestRunIdInLastMonth()(implicit userToken: UserToken): Option[String] = {
    getPipelineLatestRunIdInLastMonth(ADFService.CLEANUP_PIPELINE_NAME)
  }

  def getImportHRDataIntoSqlPipelineLatestRunIdInLastMonth()(implicit userToken: UserToken): Option[String] = {
    getPipelineLatestRunIdInLastMonth(ADFService.IMPORT_HR_DATA_INTO_SQL_PIPELINE_NAME)
  }

  def getPipelineLatestRunIdInLastMonth(pipelineName: String)(implicit userToken: UserToken): Option[String] = {
    val timeNow = ZonedDateTime.now(ZoneOffset.UTC)
    val startTime = timeNow.minusDays(30)
    val endTime = timeNow.plusDays(1)
    adfRestClient.getRunIdForLatestPipelineRunInTimeInterval(pipelineName, startTime, endTime)
  }

  def isPipelineRunInProgress(pipelineRunId: String)(implicit userToken: UserToken): Boolean = {
    val status = adfRestClient.getPipelineRunStatus(pipelineRunId)
    status.equals(ADFService.PIPELINE_RUN_STATUS_IN_PROGRESS_VALUE) || status.equals(ADFService.PIPELINE_RUN_STATUS_QUEUED_VALUE)
  }

  def isPipelineInSucceededStatus(pipelineRunId: String)(implicit userToken: UserToken): Boolean = {
    adfRestClient.getPipelineRunStatus(pipelineRunId).equals(ADFService.PIPELINE_RUN_STATUS_SUCCEEDED_VALUE)
  }

  def isPipelineRunInFailedStatus(pipelineRunId: String)(implicit userToken: UserToken): Boolean = {
    adfRestClient.getPipelineRunStatus(pipelineRunId).equals(ADFService.PIPELINE_RUN_STATUS_FAILED_VALUE)
  }

  def filterInProgressPipelines(pipelineRunIds: List[String], mustFinishSuccessfully: Boolean)
                               (implicit userToken: UserToken): List[String] = {
    pipelineRunIds
      .map(runId => (runId, adfRestClient.getPipelineRunStatus(runId)))
      .filter { case (runId: String, status: String) =>
        if (status.isEmpty) {
          throw new ResourceNotFoundException(s"Undefined status for pipeline with run id $runId")
        }
        if (mustFinishSuccessfully) {
          if (status.equals(ADFService.PIPELINE_RUN_STATUS_FAILED_VALUE)) {
            throw new ADFPipelineRunFailed(s"Pipeline with run id $runId failed!", runId)
          }
          !status.equals(ADFService.PIPELINE_RUN_STATUS_SUCCEEDED_VALUE)
        } else {
          !status.equals(ADFService.PIPELINE_RUN_STATUS_SUCCEEDED_VALUE) && !status.equals(ADFService.PIPELINE_RUN_STATUS_FAILED_VALUE)
        }
      }
      .map {
        case (runId: String, _: String) => runId
      }
  }

  implicit def dateTimeOrdering: Ordering[ZonedDateTime] = Ordering.fromLessThan(_ isBefore _)

  def getLatestPipelineRunsForTriggerStartingAfterTimestamp(triggerName: String,
                                                            ingestionModeSwitchTimestamp: ZonedDateTime,
                                                            numberOfPipelineRunIds: Int = 1)
                                                           (implicit userToken: UserToken): List[String] = {
    val triggerRuns: List[TriggerRun] = adfRestClient
      .getTriggerRunsInChronologicalOrder(triggerName, startTime = ingestionModeSwitchTimestamp, endTime = ZonedDateTime.now(ZoneOffset.UTC))
    triggerRuns.flatMap(_.triggeredPipelines.values).slice(0, numberOfPipelineRunIds + 1)

  }

  private def createEmailsPipelineTrigger(mostRecentDayInThePast6AM: ZonedDateTime)(implicit userToken: UserToken): Unit = {
    val emailsPipelineTriggerDef: String = FileUtils.readResourceContent("adf_triggers/emails_pipeline_trigger.template")
    val template: JsonTemplate = new JsonTemplate(emailsPipelineTriggerDef)

    val startTime = mostRecentDayInThePast6AM
    val startTimeStr = startTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"

    template.withVar("startTime", startTimeStr)
    template.withVar("triggerName", ADFTrigger.EMAILS_PIPELINE_TRIGGER.triggerName)

    adfRestClient.createTrigger(template.prettyString(), ADFTrigger.EMAILS_PIPELINE_TRIGGER.triggerName)
  }

  private def createEmailsPipelineBackfillPastWeekTrigger(mostRecentDayInThePast6AM: ZonedDateTime)
                                                         (implicit userToken: UserToken): Unit = {
    val emailsPipelineBackfillPastWeekTriggerDef: String = FileUtils.readResourceContent("adf_triggers/emails_pipeline_backfill_past_week_trigger.template")
    val template: JsonTemplate = new JsonTemplate(emailsPipelineBackfillPastWeekTriggerDef)

    val endTime = mostRecentDayInThePast6AM
    val triggerTimeSpan = if(emailsBackfillTimeSpan < 7) emailsBackfillTimeSpan else 7
    val startTime = endTime.minusDays(triggerTimeSpan)

    val startTimeStr = startTime.format(triggersDateTimeFormatter)
    val endTimeStr = endTime.format(triggersDateTimeFormatter)

    template.withVar("startTime", startTimeStr)
    template.withVar("endTime", endTimeStr)
    template.withVar("triggerName", ADFTrigger.EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER.triggerName)

    adfRestClient.createTrigger(template.prettyString(), ADFTrigger.EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER.triggerName)
  }

  private def createEmailsPipelineBackfillFurtherPastTrigger(mostRecentDayInThePast6AM: ZonedDateTime)
                                                            (implicit userToken: UserToken): Unit = {
    if(emailsBackfillTimeSpan > 7) {
      val emailsPipelineBackfillPastWeekTriggerDef: String = FileUtils.readResourceContent("adf_triggers/emails_pipeline_backfill_further_past_trigger.template")
      val template: JsonTemplate = new JsonTemplate(emailsPipelineBackfillPastWeekTriggerDef)

      val startTime = mostRecentDayInThePast6AM.minusDays(emailsBackfillTimeSpan)
      val endTime = mostRecentDayInThePast6AM.minusDays(7)

      val startTimeStr = startTime.format(triggersDateTimeFormatter)
      val endTimeStr = endTime.format(triggersDateTimeFormatter)

      template.withVar("startTime", startTimeStr)
      template.withVar("endTime", endTimeStr)
      template.withVar("triggerName", ADFTrigger.EMAILS_PIPELINE_BACKFILL_FURTHER_PAST_TRIGGER.triggerName)

      adfRestClient.createTrigger(template.prettyString(), ADFTrigger.EMAILS_PIPELINE_BACKFILL_FURTHER_PAST_TRIGGER.triggerName)
    }
  }

  private def createInferredRolesPipelineBackfillTrigger(mostRecentDayInThePast6AM: ZonedDateTime)
                                                        (implicit userToken: UserToken): Unit = {
    val inferredRolesPipelineBackfillTriggerDef: String = FileUtils.readResourceContent("adf_triggers/inferred_roles_pipeline_backfill_trigger.template")
    val template: JsonTemplate = new JsonTemplate(inferredRolesPipelineBackfillTriggerDef)

    val endTime = mostRecentDayInThePast6AM
    val startTime = endTime.minusDays(1)

    val startTimeStr = startTime.format(triggersDateTimeFormatter)
    val endTimeStr = endTime.format(triggersDateTimeFormatter)

    template.withVar("startTime", startTimeStr)
    template.withVar("endTime", endTimeStr)
    template.withVar("triggerName", ADFTrigger.INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER.triggerName)

    adfRestClient.createTrigger(template.prettyString(), ADFTrigger.INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER.triggerName)
  }

  private def createInferredRolesPipelineTrigger()(implicit userToken: UserToken): Unit = {
    val inferredRolesPipelineTriggerDef: String = FileUtils.readResourceContent("adf_triggers/inferred_roles_pipeline_trigger.template")
    val template: JsonTemplate = new JsonTemplate(inferredRolesPipelineTriggerDef)

    val startTime = ZonedDateTime.now(ZoneOffset.UTC)

    val startTimeStr = startTime.format(triggersDateTimeFormatter)

    template.withVar("startTime", startTimeStr)
    template.withVar("triggerName", ADFTrigger.INFERRED_ROLES_PIPELINE_TRIGGER.triggerName)

    adfRestClient.createTrigger(template.prettyString(), ADFTrigger.INFERRED_ROLES_PIPELINE_TRIGGER.triggerName)
  }

  private def createEmployeeProfilesPipelineBackfillTrigger(mostRecentDayInThePast6AM: ZonedDateTime)
                                                           (implicit userToken: UserToken): Unit = {
    val employeeProfilesPipelineBackfillTriggerDef: String = FileUtils.readResourceContent("adf_triggers/employee_profiles_pipeline_backfill_trigger.template")
    val template: JsonTemplate = new JsonTemplate(employeeProfilesPipelineBackfillTriggerDef)

    val endTime = mostRecentDayInThePast6AM
    val startTime = endTime.minusDays(1)

    val startTimeStr = startTime.format(triggersDateTimeFormatter)
    val endTimeStr = endTime.format(triggersDateTimeFormatter)

    template.withVar("startTime", startTimeStr)
    template.withVar("endTime", endTimeStr)
    template.withVar("triggerName", ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER.triggerName)

    adfRestClient.createTrigger(template.prettyString(), ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER.triggerName)
  }

  private def createEmployeeProfilesPipelineTrigger()(implicit userToken: UserToken): Unit = {
    val employeeProfilesPipelineTriggerDef: String = FileUtils.readResourceContent("adf_triggers/employee_profiles_pipeline_trigger.template")
    val template: JsonTemplate = new JsonTemplate(employeeProfilesPipelineTriggerDef)

    val startTime = ZonedDateTime.now(ZoneOffset.UTC)

    val startTimeStr = startTime.format(triggersDateTimeFormatter)

    template.withVar("startTime", startTimeStr)
    template.withVar("triggerName", ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_TRIGGER.triggerName)

    adfRestClient.createTrigger(template.prettyString(), ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_TRIGGER.triggerName)
  }

  private def createAirtablePipelineBackfillTrigger(mostRecentDayInThePast6AM: ZonedDateTime)
                                                   (implicit userToken: UserToken): Unit = {
    val employeeProfilesPipelineBackfillTriggerDef: String = FileUtils.readResourceContent("adf_triggers/airtable_pipeline_backfill_trigger.template")
    val template: JsonTemplate = new JsonTemplate(employeeProfilesPipelineBackfillTriggerDef)

    val endTime = mostRecentDayInThePast6AM
    val startTime = endTime.minusDays(1)

    val startTimeStr = startTime.format(triggersDateTimeFormatter)
    val endTimeStr = endTime.format(triggersDateTimeFormatter)

    template.withVar("startTime", startTimeStr)
    template.withVar("endTime", endTimeStr)
    template.withVar("triggerName", ADFTrigger.AIRTABLE_PIPELINE_BACKFILL_TRIGGER.triggerName)

    adfRestClient.createTrigger(template.prettyString(), ADFTrigger.AIRTABLE_PIPELINE_BACKFILL_TRIGGER.triggerName)
  }

  private def createAirtablePipelineTrigger()(implicit userToken: UserToken): Unit = {
    val employeeProfilesPipelineTriggerDef: String = FileUtils.readResourceContent("adf_triggers/airtable_pipeline_trigger.template")
    val template: JsonTemplate = new JsonTemplate(employeeProfilesPipelineTriggerDef)

    val startTime = ZonedDateTime.now(ZoneOffset.UTC)

    val startTimeStr = startTime.format(triggersDateTimeFormatter)

    template.withVar("startTime", startTimeStr)
    template.withVar("triggerName", ADFTrigger.AIRTABLE_PIPELINE_TRIGGER.triggerName)

    adfRestClient.createTrigger(template.prettyString(), ADFTrigger.AIRTABLE_PIPELINE_TRIGGER.triggerName)
  }

  def createAndStartEmailPipelineTriggers(newIngestionMode: IngestionMode, mostRecentDayInThePast6AM: ZonedDateTime)
                                         (implicit userToken: UserToken): Unit = {
    createEmailsPipelineBackfillPastWeekTrigger(mostRecentDayInThePast6AM)
    adfRestClient.startTrigger(ADFTrigger.EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER.triggerName)
    createEmailsPipelineTrigger(mostRecentDayInThePast6AM)
    if (newIngestionMode.equals(IngestionMode.Production)) {
      adfRestClient.startTrigger(ADFTrigger.EMAILS_PIPELINE_TRIGGER.triggerName)
    }
  }

  def createAndStartEmployeeProfilePipelineTriggers(newIngestionMode: IngestionMode, mostRecentDayInThePast6AM: ZonedDateTime)
                                                   (implicit userToken: UserToken): Unit = {
    createEmployeeProfilesPipelineBackfillTrigger(mostRecentDayInThePast6AM)
    adfRestClient.startTrigger(ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER.triggerName)
    createEmployeeProfilesPipelineTrigger()
    if (newIngestionMode.equals(IngestionMode.Production)) {
      adfRestClient.startTrigger(ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_TRIGGER.triggerName)
    }
  }

  def createAndStartInferredRolesPipelineTriggers(newIngestionMode: IngestionMode, mostRecentDayInThePast6AM: ZonedDateTime)
                                                 (implicit userToken: UserToken): Unit = {
    createInferredRolesPipelineBackfillTrigger(mostRecentDayInThePast6AM)
    adfRestClient.startTrigger(ADFTrigger.INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER.triggerName)
    createInferredRolesPipelineTrigger()
    if (newIngestionMode.equals(IngestionMode.Production)) {
      adfRestClient.startTrigger(ADFTrigger.INFERRED_ROLES_PIPELINE_TRIGGER.triggerName)
    }
  }

  def createAndStartAirtablePipelineTriggers(newIngestionMode: IngestionMode, mostRecentDayInThePast6AM: ZonedDateTime)
                                            (implicit userToken: UserToken): Unit = {
    createAirtablePipelineBackfillTrigger(mostRecentDayInThePast6AM)
    adfRestClient.startTrigger(ADFTrigger.AIRTABLE_PIPELINE_BACKFILL_TRIGGER.triggerName)
    createAirtablePipelineTrigger()
    if (newIngestionMode.equals(IngestionMode.Production)) {
      adfRestClient.startTrigger(ADFTrigger.AIRTABLE_PIPELINE_TRIGGER.triggerName)
    }
  }

  def createAndStartTriggers(newIngestionMode: IngestionMode, mostRecentDayInThePast6AM: ZonedDateTime)
                            (implicit userToken: UserToken): Unit = {
    //airtable triggers need to be created first because employee_profiles_pipeline_backfill_trigger depends on airtable_pipeline_backfill_trigger
    createAndStartAirtablePipelineTriggers(newIngestionMode, mostRecentDayInThePast6AM)
    createAndStartEmailPipelineTriggers(newIngestionMode, mostRecentDayInThePast6AM)
    createAndStartEmployeeProfilePipelineTriggers(newIngestionMode, mostRecentDayInThePast6AM)
    createAndStartInferredRolesPipelineTriggers(newIngestionMode, mostRecentDayInThePast6AM)
  }

  def createAndStartBackFillFurtherPastEmailTrigger(newIngestionMode: IngestionMode, mostRecentDayInThePast6AM: ZonedDateTime)
                                                   (implicit userToken: UserToken): Unit = {
    if(emailsBackfillTimeSpan > 7) {
      createEmailsPipelineBackfillFurtherPastTrigger(mostRecentDayInThePast6AM)
      if (newIngestionMode.equals(IngestionMode.Production)) {
        adfRestClient.startTrigger(ADFTrigger.EMAILS_PIPELINE_BACKFILL_FURTHER_PAST_TRIGGER.triggerName)
      }
    }
  }

  def stopAllTriggers()(implicit userToken: UserToken): Unit = {
    adfRestClient.stopTrigger(ADFTrigger.INFERRED_ROLES_PIPELINE_TRIGGER.triggerName)
    adfRestClient.stopTrigger(ADFTrigger.INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER.triggerName)
    adfRestClient.stopTrigger(ADFTrigger.EMAILS_PIPELINE_TRIGGER.triggerName)
    adfRestClient.stopTrigger(ADFTrigger.EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER.triggerName)
    adfRestClient.stopTrigger(ADFTrigger.EMAILS_PIPELINE_BACKFILL_FURTHER_PAST_TRIGGER.triggerName)
    adfRestClient.stopTrigger(ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_TRIGGER.triggerName)
    adfRestClient.stopTrigger(ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER.triggerName)
    adfRestClient.stopTrigger(ADFTrigger.AIRTABLE_PIPELINE_TRIGGER.triggerName)
    adfRestClient.stopTrigger(ADFTrigger.AIRTABLE_PIPELINE_BACKFILL_TRIGGER.triggerName)
  }

  def deleteAllTriggers()(implicit userToken: UserToken): Unit = {
    adfRestClient.deleteTrigger(ADFTrigger.INFERRED_ROLES_PIPELINE_TRIGGER.triggerName)
    adfRestClient.deleteTrigger(ADFTrigger.INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER.triggerName)
    adfRestClient.deleteTrigger(ADFTrigger.EMAILS_PIPELINE_TRIGGER.triggerName)
    adfRestClient.deleteTrigger(ADFTrigger.EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER.triggerName)
    adfRestClient.deleteTrigger(ADFTrigger.EMAILS_PIPELINE_BACKFILL_FURTHER_PAST_TRIGGER.triggerName)
    adfRestClient.deleteTrigger(ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_TRIGGER.triggerName)
    adfRestClient.deleteTrigger(ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER.triggerName)
    adfRestClient.deleteTrigger(ADFTrigger.AIRTABLE_PIPELINE_TRIGGER.triggerName)
    adfRestClient.deleteTrigger(ADFTrigger.AIRTABLE_PIPELINE_BACKFILL_TRIGGER.triggerName)
  }

  def cancelRunningPipelines()(implicit userToken: UserToken): Unit = {
    val runningPipelines: List[PipelineRun] = adfRestClient.listRunningPipelines()
    logger.info(s"A total number of ${runningPipelines.length} running pipelines are about to be canceled.")
    runningPipelines.foreach(pipeline => adfRestClient.cancelRunningPipeline(pipeline))
  }

  def cancelTriggerRunsFromLastMonth()(implicit userToken: UserToken): Unit = {
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val startTime = now.minusDays(30)
    val endTime = now.plusDays(1)
    var triggerRuns: List[TriggerRun] = List()
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.INFERRED_ROLES_PIPELINE_TRIGGER.triggerName, startTime, endTime)
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER.triggerName, startTime, endTime)
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.EMAILS_PIPELINE_TRIGGER.triggerName, startTime, endTime)
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER.triggerName, startTime, endTime)
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.EMAILS_PIPELINE_BACKFILL_FURTHER_PAST_TRIGGER.triggerName, startTime, endTime)
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_TRIGGER.triggerName, startTime, endTime)
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER.triggerName, startTime, endTime)
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.AIRTABLE_PIPELINE_TRIGGER.triggerName, startTime, endTime)
    triggerRuns = triggerRuns ::: adfRestClient
      .getTriggerRunsInProgressInChronologicalOrder(ADFTrigger.AIRTABLE_PIPELINE_BACKFILL_TRIGGER.triggerName, startTime, endTime)
    logger.info(s"A total number of ${triggerRuns.length} running pipelines are about to be canceled.")
    triggerRuns.foreach(triggerRun => adfRestClient.cancelTriggerRun(triggerRun))
  }

  def startEnd2EndEmployeeProfilePipeline()(implicit userToken: UserToken): Option[String] = {
    adfRestClient.createPipelineRun(ADFService.END2END_EMPLOYEE_PIPELINE_NAME)
  }

  def startCleanupPipeline()(implicit userToken: UserToken): Option[String] = {
    adfRestClient.createPipelineRun(ADFService.CLEANUP_PIPELINE_NAME)
  }

  def getMostRecentDayInThePast6AMTFromADF()(implicit userToken: UserToken): Option[ZonedDateTime] = {
    adfRestClient.getEmailsPipelineTriggerStartTime()
  }

  def updateADFIngestionModeGlobalParameter(ingestionMode: IngestionMode)(implicit userToken: UserToken): Unit = {
    adfRestClient.updateADFIngestionModeGlobalParameter(ingestionMode)
  }

}

object ADFService {
  // TODO keep this in sync with the name of the pipeline in ADF
  val CLEANUP_PIPELINE_NAME = "End2EndCleanup"
  val END2END_EMPLOYEE_PIPELINE_NAME = "End2EndEmployeeProfilePipeline"
  val IMPORT_HR_DATA_INTO_SQL_PIPELINE_NAME = "ImportHRDataIntoSql"

  val TRIGGER_RUN_STATUS_RUNNING = "Running"

  val PIPELINE_RUN_STATUS_IN_PROGRESS_VALUE = "InProgress"
  val PIPELINE_RUN_STATUS_QUEUED_VALUE = "Queued"
  val PIPELINE_RUN_STATUS_SUCCEEDED_VALUE = "Succeeded"
  val PIPELINE_RUN_STATUS_FAILED_VALUE = "Failed"

  def computeMostRecentDayInThePast6AM(ingestionModeSwitchTimestamp: ZonedDateTime): ZonedDateTime = {
    val ingestionModeSwitchTimestampUTC = ingestionModeSwitchTimestamp.withZoneSameInstant(ZoneOffset.UTC)
    val inputDay6AmUTC: ZonedDateTime = ingestionModeSwitchTimestampUTC.withHour(6).withMinute(0).withSecond(0).withNano(0)

    val mostRecentDayInThePast6AM = if (ingestionModeSwitchTimestampUTC.isBefore(inputDay6AmUTC)) {
      inputDay6AmUTC.minusDays(1)
    } else {
      inputDay6AmUTC
    }

    mostRecentDayInThePast6AM
  }
}
