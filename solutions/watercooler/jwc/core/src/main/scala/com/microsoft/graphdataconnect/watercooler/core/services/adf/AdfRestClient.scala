/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.services.adf

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.microsoft.graphdataconnect.watercooler.core.exceptions.PipelineRunCreationFailedException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jsontemplate.JsonTemplate
import com.microsoft.graphdataconnect.watercooler.common.exceptions.UnauthorizedException
import com.microsoft.graphdataconnect.watercooler.core.exceptions.{ADFRestRequestFailedException, PipelineRunCreationFailedException}
import com.microsoft.graphdataconnect.watercooler.core.models.UserToken
import com.microsoft.graphdataconnect.watercooler.core.models.adf.PipelineRunsResponse
import com.microsoft.graphdataconnect.watercooler.core.utils.FileUtils
import kong.unirest._
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

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
  def getStatusForLatestPipelineRunInTimeInterval(pipelineName: String,
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
            Some(pipelineRunsResponse.value.head.status)
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


}
