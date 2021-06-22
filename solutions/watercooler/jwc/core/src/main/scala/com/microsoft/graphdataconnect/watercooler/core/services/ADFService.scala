/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.services

import java.time.{ZoneOffset, ZonedDateTime}

import com.microsoft.graphdataconnect.watercooler.core.models.UserToken
import com.microsoft.graphdataconnect.watercooler.core.services.adf.AdfRestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ADFService(@Autowired val adfRestClient: AdfRestClient) {

  def startUpdateUserEventsAttendancePipeline()(implicit userToken: UserToken): Option[String] = {
    adfRestClient.createPipelineRun(ADFService.UPDATE_USER_EVENTS_ATTENDANCE_PIPELINE_NAME)
  }

  def isUpdateUserEventsAttendancePipelineRunning()(implicit userToken: UserToken): Boolean = {
    val timeNow = ZonedDateTime.now(ZoneOffset.UTC)
    val startTime = timeNow.minusDays(30)
    val endTime = timeNow.plusDays(1)
    val latestPipelineRunStatusOpt = adfRestClient.getStatusForLatestPipelineRunInTimeInterval(ADFService.UPDATE_USER_EVENTS_ATTENDANCE_PIPELINE_NAME, startTime, endTime)
    if(latestPipelineRunStatusOpt.isDefined) {
      val latestPipelineRunStatus = latestPipelineRunStatusOpt.get
      latestPipelineRunStatus == ADFService.PIPELINE_RUN_STATUS_IN_PROGRESS_VALUE || latestPipelineRunStatus == ADFService.PIPELINE_RUN_STATUS_QUEUED_VALUE
    } else false
  }

}

object ADFService {
  // TODO keep this in sync with the name of the pipeline in ADF
  val UPDATE_USER_EVENTS_ATTENDANCE_PIPELINE_NAME = "UpdateUserEventsAttendance"


  val PIPELINE_RUN_STATUS_IN_PROGRESS_VALUE = "InProgress"
  val PIPELINE_RUN_STATUS_QUEUED_VALUE = "Queued"
  val PIPELINE_RUN_STATUS_SUCCEEDED_VALUE = "Succeeded"
  val PIPELINE_RUN_STATUS_FAILED_VALUE = "Failed"
}