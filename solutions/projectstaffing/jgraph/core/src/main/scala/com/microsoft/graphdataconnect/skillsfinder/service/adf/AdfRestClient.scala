package com.microsoft.graphdataconnect.skillsfinder.service.adf

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset, ZonedDateTime}

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jsontemplate.JsonTemplate
import com.microsoft.graphdataconnect.model.admin.IngestionMode.IngestionMode
import com.microsoft.graphdataconnect.skillsfinder.exceptions._
import com.microsoft.graphdataconnect.skillsfinder.models.dto.adf.{PipelineRun, PipelineRunsResponse, TriggerRun, TriggerRunsResponse}
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.UserToken
import com.microsoft.graphdataconnect.skillsfinder.utils.FileUtils
import kong.unirest._
import org.json.JSONObject
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

@Service
class AdfRestClient {

  @Value("${adf.subscriptionId}")
  var adfSubscriptionID: String = _

  @Value("${adf.resource.group.name}")
  var adfResourceGroup: String = _

  @Value("${adf.name}")
  var adfName: String = _

  @Autowired
  var mapper: ObjectMapper = _

  private val triggersDateTimeFormatter = DateTimeFormatter.ISO_INSTANT

  private val logger: Logger = LoggerFactory.getLogger(classOf[AdfRestClient])

  @AzureApiRequestRetry
  def createTrigger(body: String, triggerName: String)(implicit userToken: UserToken): Unit = {
    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/triggers/$triggerName?api-version=2018-06-01"

    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.put(url).headers(headers.asJava).body(body).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          logger.info(f"Request to create trigger $triggerName succeeded")
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to create trigger $triggerName. Status: ${response.getStatusText}")
        } else {
          throw new TriggerCreationRequestFailed(f"Failed to create trigger $triggerName. Status: ${response.getStatusText}")
        }

      case Failure(exception: Throwable) =>
        throw new TriggerCreationRequestFailed(f"Failed to create trigger $triggerName.", exception)

    }
  }

  @AzureApiRequestRetry
  def deleteTrigger(triggerName: String)(implicit userToken: UserToken): Unit = {
    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/triggers/$triggerName?api-version=2018-06-01"

    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.delete(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          logger.info(f"Request to delete trigger $triggerName succeeded")
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to delete trigger $triggerName. Status: ${response.getStatusText}")
        } else {
          //If the response has 204 status then the trigger does not exist.
          //Otherwise we have to explicitly inform the user that something went wrong.
          if (response.getStatus != HttpStatus.NO_CONTENT) {
            throw new DeleteTriggerFailureException(triggerName)
          } else {
            logger.info(f"Failed to delete trigger $triggerName as it does not exist. Status: ${response.getStatusText}")
          }
        }

      case Failure(exception: Throwable) =>
        throw new DeleteTriggerFailureException(triggerName, exception)
    }
  }

  @AzureApiRequestRetry
  def startTrigger(triggerName: String)(implicit userToken: UserToken): Unit = {
    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/triggers/$triggerName/start?api-version=2018-06-01"

    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          logger.info(f"Request to start trigger $triggerName succeeded")
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to start trigger $triggerName. Status: ${response.getStatusText}")
        } else {
          throw new StartTriggerRequestFailed(f"Failed to start trigger $triggerName. Status: ${response.getStatusText}")
        }

      case Failure(exception: Throwable) =>
        throw new StartTriggerRequestFailed(f"Failed to start trigger $triggerName", exception)
    }
  }

  @AzureApiRequestRetry
  def getEmailsPipelineTriggerStartTime()(implicit userToken: UserToken): Option[ZonedDateTime] = {
    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/triggers/emails_pipeline_trigger?api-version=2018-06-01"

    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.get(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          val triggerJsonNode = response.getBody
          val triggerJsonObject: json.JSONObject = triggerJsonNode.getObject
          val propertiesObject = triggerJsonObject.getJSONObject("properties")
          val typePropertiesObject = propertiesObject.getJSONObject("typeProperties")
          val startTime: String = typePropertiesObject.getString("startTime")

          def instant = Instant.from(triggersDateTimeFormatter.parse(startTime))

          def mostRecentDayInThePast6AM: ZonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)

          logger.info(f"Request to get most recent day in the past 6AM date from emails_pipeline_trigger succeeded")
          Some(mostRecentDayInThePast6AM)
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Request to get most recent day in the past 6AM date from emails_pipeline_trigger failed! Status: ${response.getStatusText}")
        } else {
          throw new ADFRestRequestFailedException(f"Request to get most recent day in the past 6AM date from emails_pipeline_trigger failed! Status: ${response.getStatusText}")
        }

      case Failure(exception: Throwable) =>
        throw new ADFRestRequestFailedException(f"Request to get most recent day in the past 6AM date from emails_pipeline_trigger failed!", exception)
    }
  }

  @AzureApiRequestRetry
  def stopTrigger(triggerName: String)(implicit userToken: UserToken): Unit = {
    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/triggers/$triggerName/stop?api-version=2018-06-01"

    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          logger.info(f"Request to stop trigger $triggerName succeeded")
        } else {
          val responseObject: json.JSONObject = response.getBody.getObject
          //If the trigger does not exist then a informative message will be logged.
          //Otherwise the user has to be explicitly informed about the failure.
          if (response.getStatus == HttpStatus.BAD_REQUEST && responseObject.has("error") && responseObject.getJSONObject("error").has("message") &&
            responseObject.getJSONObject("error").getString("message").equals(s"Entity $triggerName not found")) {
            logger.info(f"Failed to stop trigger $triggerName failed. Status: ${response.getStatusText}. " +
              f"Message: ${responseObject.getJSONObject("error").getString("message")}")
          } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
            throw new UnauthorizedException(f"Failed to stop trigger $triggerName. Status: ${response.getStatusText}")
          } else {
            logger.error(f"Failed to stop trigger $triggerName. Status: ${response.getStatusText}")
            throw new StopTriggerFailureException(triggerName)
          }
        }

      case Failure(exception: Throwable) =>
        throw new StopTriggerFailureException(triggerName, exception)
    }
  }

  @AzureApiRequestRetry
  def updateADFIngestionModeGlobalParameter(ingestionMode: IngestionMode)(implicit userToken: UserToken): Unit = {
    val ingestionModeGlobalParameterName: String = "gdc_data_ingestion_mode"
    val ingestionModeGlobalParameterValue: String = ingestionMode.toString

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName?api-version=2018-06-01"

    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    //First we make a request to get the current state of the Data Factory
    Try {
      Unirest.get(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          logger.info("The request for getting the Data Factory current state succeeded. ")

          val dataFactoryJsonNode: JsonNode = response.getBody

          val dataFactoryJsonObject: json.JSONObject = dataFactoryJsonNode.getObject
          val propertiesObject: json.JSONObject = dataFactoryJsonObject.getJSONObject("properties")
          val globalParametersObject: json.JSONObject = propertiesObject.getJSONObject("globalParameters")
          val globalParameterObject: json.JSONObject = globalParametersObject.getJSONObject(ingestionModeGlobalParameterName)

          // We build the update request using the current state of the Data Factory
          // in order to be sure that we don't change anything else
          globalParameterObject.put("value", ingestionModeGlobalParameterValue)
          globalParametersObject.put(ingestionModeGlobalParameterName, globalParameterObject)

          val updatedPropertiesObject = new JSONObject()
          updatedPropertiesObject.put("globalParameters", globalParametersObject.toMap)
          if (propertiesObject.has("encryption")) {
            updatedPropertiesObject.put("encryption", propertiesObject.getJSONObject("encryption").toMap)
          }
          if (propertiesObject.has("publicNetworkAccess")) {
            updatedPropertiesObject.put("publicNetworkAccess", propertiesObject.getJSONObject("publicNetworkAccess").toMap)
          }
          if (propertiesObject.has("repoConfiguration")) {
            updatedPropertiesObject.put("repoConfiguration", propertiesObject.getJSONObject("repoConfiguration").toMap)
          }

          val updatedDataFactoryObject = new JSONObject()
          updatedDataFactoryObject.put("properties", updatedPropertiesObject)
          if (dataFactoryJsonObject.has("identity")) {
            updatedDataFactoryObject.put("identity", dataFactoryJsonObject.getJSONObject("identity").toMap)
          }
          if (dataFactoryJsonObject.has("location")) {
            updatedDataFactoryObject.put("location", dataFactoryJsonObject.getString("location"))
          }
          if (dataFactoryJsonObject.has("tags")) {
            updatedDataFactoryObject.put("tags", dataFactoryJsonObject.getJSONObject("tags").toMap)
          }

          val updatedDataFactoryJsonNode = new JsonNode(updatedDataFactoryObject.toString)
          println(updatedDataFactoryJsonNode)

          //Global parameter update request
          Try {
            Unirest.put(url).headers(headers.asJava).body(updatedDataFactoryJsonNode).asJson()
          } match {
            case Success(updateResponse: HttpResponse[JsonNode]) =>
              if (updateResponse.getStatus == HttpStatus.OK) {
                // [GDC-1493] It appears that sometimes the Data Factory does not get fully updated by the time the triggers are recreated, although the REST call returns success,
                // leading some pipelines to fail because they can't find the 'gdc_data_ingestion_mode' property. Inserting a delay should help prevent this
                Thread.sleep(10000)
                logger.info(f"The request for updating the $ingestionModeGlobalParameterName global param to $ingestionModeGlobalParameterValue succeeded.")
              } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException(s"The request for updating the $ingestionModeGlobalParameterName global param to $ingestionModeGlobalParameterValue failed!")
              } else {
                throw new UpdateADFIngestionModeGlobalParameterException(s"The request for updating the $ingestionModeGlobalParameterName global param to $ingestionModeGlobalParameterValue failed!")
              }
            case Failure(exception: Throwable) =>
              throw new UpdateADFIngestionModeGlobalParameterException(s"The request for updating the $ingestionModeGlobalParameterName global param to $ingestionModeGlobalParameterValue failed!", exception)
          }

        } else {
          throw new UpdateADFIngestionModeGlobalParameterException("The request for getting the Data Factory current state failed!")
        }

      case Failure(exception: Throwable) =>
        throw new UpdateADFIngestionModeGlobalParameterException("The request for getting the Data Factory current state failed!", exception)
    }
  }

  @AzureApiRequestRetry
  def listRunningPipelines()(implicit userToken: UserToken): List[PipelineRun] = {
    val listRunningPipelinesPayloadTemplate: String = FileUtils.readResourceContent("adf_payloads/list_running_pipelines.template")
    val template: JsonTemplate = new JsonTemplate(listRunningPipelinesPayloadTemplate)

    val timeNow = ZonedDateTime.now(ZoneOffset.UTC)
    val startTime = timeNow.minusDays(7)
    val endTime = timeNow.plusDays(1)
    val startTimeStr = startTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"
    val endTimeStr = endTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"

    template.withVar("startTime", startTimeStr)
    template.withVar("endTime", endTimeStr)

    val requestBody = template.prettyString()

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/queryPipelineRuns?api-version=2018-06-01"
    //TODO The default response page size for queryPipelineRuns endpoint seems to be 100 (the documentation doesn't mention this, we found out from making multiple requests)
    // we have to add logic for processing all the result pages using the continuationToken
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).body(requestBody).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          val responseBody: PipelineRunsResponse = mapper.readValue(response.getBody.toPrettyString, classOf[PipelineRunsResponse])
          responseBody.value
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to list running pipelines. Status: ${response.getStatusText}. Response: ${response.getBody}")
        } else {
          throw new ADFRestRequestFailedException(f"Failed to list running pipelines. Status: ${response.getStatusText}. Response: ${response.getBody}")
        }
      case Failure(exception: Throwable) =>
        throw new ADFRestRequestFailedException(f"Failed to list running pipelines failed.", exception)
    }
  }

  @AzureApiRequestRetry
  def getRunIdForLatestPipelineRunInTimeInterval(pipelineName: String,
                                                 startTime: ZonedDateTime,
                                                 endTime: ZonedDateTime)
                                                (implicit userToken: UserToken): Option[String] = {
    val listRunningPipelinesPayloadTemplate: String = FileUtils.readResourceContent("adf_payloads/list_pipeline_runs_in_order.template")
    val template: JsonTemplate = new JsonTemplate(listRunningPipelinesPayloadTemplate)

    val startTimeStr = startTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"
    val endTimeStr = endTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"

    template.withVar("startTime", startTimeStr)
    template.withVar("endTime", endTimeStr)
    template.withVar("pipelineName", pipelineName)

    val requestBody = template.prettyString()

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/queryPipelineRuns?api-version=2018-06-01"
    //TODO The default response page size for queryPipelineRuns endpoint seems to be 100 (the documentation doesn't mention this, we found out from making multiple requests)
    // we have to add logic for processing all the result pages using the continuationToken
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).body(requestBody).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          val pipelineRunsResponse: PipelineRunsResponse = mapper.readValue(response.getBody.toPrettyString, classOf[PipelineRunsResponse])
          if (pipelineRunsResponse.value.nonEmpty) {
            Some(pipelineRunsResponse.value.head.runId)
          } else None
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to list running pipelines. Status: ${response.getStatusText}. Response: ${response.getBody}")
        } else {
          throw new ADFRestRequestFailedException(f"Failed to list running pipelines. Status: ${response.getStatusText}. Response: ${response.getBody}")
        }
      case Failure(exception: Throwable) =>
        throw new ADFRestRequestFailedException(f"Failed to list running pipelines failed.", exception)
    }
  }


  @AzureApiRequestRetry
  def cancelRunningPipeline(runningPipeline: PipelineRun)(implicit userToken: UserToken): Unit = {
    logger.info("Stopping pipeline {}", runningPipeline.pipelineName)

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/pipelineruns/${runningPipeline.runId}/cancel?api-version=2018-06-01"
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          logger.info(f"Request to cancel pipeline ${runningPipeline.pipelineName} with runId ${runningPipeline.runId} succeeded")
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to cancel pipeline ${runningPipeline.pipelineName} with runId ${runningPipeline.runId}. Status: ${response.getStatusText}")
        } else {
          logger.error(f"Failed to cancel pipeline ${runningPipeline.pipelineName} with runId ${runningPipeline.runId}. Status: ${response.getStatusText}")
        }
      case Failure(exception: Throwable) =>
        throw new PipelineRunCancellationFailedException(runningPipeline.pipelineName, runningPipeline.runId, exception)
    }
  }

  @AzureApiRequestRetry
  def cancelTriggerRun(triggerRun: TriggerRun)(implicit userToken: UserToken): Unit = {
    logger.info(s"Cancelling run ${triggerRun.triggerRunId} of trigger ${triggerRun.triggerName}")

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/triggers/${triggerRun.triggerName}/triggerRuns/${triggerRun.triggerRunId}/cancel?api-version=2018-06-01"
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          logger.info(f"Request to cancel run ${triggerRun.triggerRunId} of trigger ${triggerRun.triggerName} succeeded")
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to cancel run ${triggerRun.triggerRunId} of trigger ${triggerRun.triggerName}. Status: ${response.getStatusText}")
        } else {
          logger.error(f"Failed to cancel run ${triggerRun.triggerRunId} of trigger ${triggerRun.triggerName}. Status: ${response.getStatusText}")
        }
      case Failure(exception: Throwable) =>
        throw new TriggerRunCancellationFailedException(triggerRun.triggerName, triggerRun.triggerRunId, exception)
    }
  }

  @AzureApiRequestRetry
  def createPipelineRun(pipelineName: String)(implicit userToken: UserToken): Option[String] = {
    logger.info("Create new pipeline run for pipeline {}", pipelineName)

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/" +
      f"factories/$adfName/pipelines/$pipelineName/createRun?api-version=2018-06-01"
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          val runId = response.getBody.getObject.get("runId").toString
          logger.info(f"Request to create pipeline run for pipeline: $pipelineName succeeded. RunId: $runId")
          Some(runId)
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to create pipeline run for pipeline: $pipelineName. Status: ${response.getStatusText}")
        } else {
          logger.error(f"Failed to create pipeline run for pipeline: $pipelineName. Status: ${response.getStatusText}")
          throw new PipelineRunCreationFailedException(pipelineName)
        }
      case Failure(exception: Throwable) =>
        throw new PipelineRunCreationFailedException(pipelineName, exception)
    }
  }

  @AzureApiRequestRetry
  def getPipelineRunStatus(pipelineRunId: String)(implicit userToken: UserToken): String = {
    logger.debug("Checking if pipeline run: {} is still in progress", pipelineRunId)

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/pipelineruns/$pipelineRunId?api-version=2018-06-01"
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.get(url).headers(headers.asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          val responseBody: PipelineRun = mapper.readValue(response.getBody.toPrettyString, classOf[PipelineRun])
          logger.debug(f"Request to get info for pipeline run with id : $pipelineRunId succeeded. Response: $responseBody")
          responseBody.status
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Request to get info for pipeline run with id : $pipelineRunId failed. Status: ${response.getStatusText}")
        } else if (response.getStatus == HttpStatus.NOT_FOUND) {
          throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, f"There is no pipeline run with id : $pipelineRunId . Status: ${response.getStatusText}")
        } else {
          logger.error(f"Request to get info for pipeline run with id : $pipelineRunId failed. Status: ${response.getStatusText}")
          throw new FailedToGetPipelineRunStatusException(pipelineRunId)
        }
      case Failure(exception: Throwable) =>
        throw new FailedToGetPipelineRunStatusException(pipelineRunId, exception)
    }
  }

  @AzureApiRequestRetry
  def getTriggerRunsInProgressInChronologicalOrder(triggerName: String, startTime: ZonedDateTime, endTime: ZonedDateTime)
                                                  (implicit userToken: UserToken): List[TriggerRun] = {
    val listTriggerRunsPayloadTemplate: String = FileUtils.readResourceContent("adf_payloads/query_trigger_runs_in_progress.template")
    val template: JsonTemplate = new JsonTemplate(listTriggerRunsPayloadTemplate)

    val startTimeStr = startTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"
    val endTimeStr = endTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"

    template.withVar("startTime", startTimeStr)
    template.withVar("endTime", endTimeStr)
    template.withVar("triggerName", triggerName)

    val requestBody = template.prettyString()

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/queryTriggerRuns?api-version=2018-06-01"
    //TODO The default response page size for queryTriggerRuns endpoint seems to be 100 (the documentation doesn't mention this, we found out from making multiple requests)
    // we have to add logic for processing all the result pages using the continuationToken
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).body(requestBody).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          val triggerRunsResponse: TriggerRunsResponse = mapper.readValue(response.getBody.toPrettyString, classOf[TriggerRunsResponse])
          triggerRunsResponse.value
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to list in progress trigger runs for trigger $triggerName. Status: ${response.getStatusText}. Response: ${response.getBody}")
        } else {
          throw new ADFRestRequestFailedException(f"Failed to list in progress trigger runs for trigger  $triggerName. Status: ${response.getStatusText}. Response: ${response.getBody}")
        }
      case Failure(exception: Throwable) =>
        throw new ADFRestRequestFailedException(f"Failed to list in progress trigger runs for trigger  $triggerName.", exception)
    }
  }

  @AzureApiRequestRetry
  def getTriggerRunsInChronologicalOrder(triggerName: String, startTime: ZonedDateTime, endTime: ZonedDateTime)
                                        (implicit userToken: UserToken): List[TriggerRun] = {
    val listTriggerRunsPayloadTemplate: String = FileUtils.readResourceContent("adf_payloads/query_trigger_runs.template")
    val template: JsonTemplate = new JsonTemplate(listTriggerRunsPayloadTemplate)

    val startTimeStr = startTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"
    val endTimeStr = endTime.format(triggersDateTimeFormatter) //"2020-11-30T06:00:00Z"

    template.withVar("startTime", startTimeStr)
    template.withVar("endTime", endTimeStr)
    template.withVar("triggerName", triggerName)

    val requestBody = template.prettyString()

    val url = f"https://management.azure.com/subscriptions/$adfSubscriptionID/resourceGroups/$adfResourceGroup/providers/Microsoft.DataFactory/factories/" +
      f"$adfName/queryTriggerRuns?api-version=2018-06-01"
    //TODO The default response page size for queryTriggerRuns endpoint seems to be 100 (the documentation doesn't mention this, we found out from making multiple requests)
    // we have to add logic for processing all the result pages using the continuationToken
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> f"Bearer ${userToken.accessToken}")

    Try {
      Unirest.post(url).headers(headers.asJava).body(requestBody).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        if (response.getStatus == HttpStatus.OK) {
          val triggerRunsResponse: TriggerRunsResponse = mapper.readValue(response.getBody.toPrettyString, classOf[TriggerRunsResponse])
          triggerRunsResponse.value
        } else if (response.getStatus == HttpStatus.UNAUTHORIZED) {
          throw new UnauthorizedException(f"Failed to list trigger runs for trigger $triggerName. Status: ${response.getStatusText}. Response: ${response.getBody}")
        } else {
          throw new ADFRestRequestFailedException(f"Failed to list trigger runs for trigger  $triggerName. Status: ${response.getStatusText}. Response: ${response.getBody}")
        }
      case Failure(exception: Throwable) =>
        throw new ADFRestRequestFailedException(f"Failed to list trigger runs for trigger  $triggerName.", exception)
    }
  }

}
