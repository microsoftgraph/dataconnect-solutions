/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.services

import java.time.{ZoneOffset, ZonedDateTime}

import com.microsoft.graphdataconnect.model.admin.IngestionModeSwitchPhase._
import com.microsoft.graphdataconnect.model.admin.{ADFTrigger, IngestionMode, IngestionModeSwitchPhase}
import com.microsoft.graphdataconnect.skillsfinder.Runner
import com.microsoft.graphdataconnect.skillsfinder.config.ApplicationLifecycleEventListener
import com.microsoft.graphdataconnect.skillsfinder.controllers.{GDCRestClient, GdcController}
import com.microsoft.graphdataconnect.skillsfinder.exceptions.{DeleteTriggerFailureException, IngestionModeSwitchFailedException}
import com.microsoft.graphdataconnect.skillsfinder.models.TokenScope
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.{IngestionModeSwitchState, UserToken}
import com.microsoft.graphdataconnect.skillsfinder.models.response.IngestionModeSwitchStateResponse
import com.microsoft.graphdataconnect.skillsfinder.service.adf.ADFService
import com.microsoft.graphdataconnect.skillsfinder.service.ingestionmode.{IngestionModeSwitchService, ModeSwitchStateService}
import com.microsoft.graphdataconnect.skillsfinder.service.{ConfigurationService, WebSocketService}
import com.microsoft.graphdataconnect.skillsfinder.services.IngestionModeSwitchServiceTest._
import org.apache.commons.lang3.exception.ExceptionUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.{verify, when}
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

@RunWith(classOf[SpringRunner])
@SpringBootTest(
  properties = Array("spring.profiles.active=test"),
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = Array(classOf[Runner]))
class IngestionModeSwitchServiceTest {

  @MockBean
  var adfService: ADFService = _
  @MockBean
  var webSocketService: WebSocketService = _
  @MockBean
  var modeSwitchStateService: ModeSwitchStateService = _
  @MockBean
  var configurationService: ConfigurationService = _
  @Autowired
  var ingestionModeSwitchService: IngestionModeSwitchService = _

  //These beans are commented so not to be instantiated by Spring because they are not relevant for these tests
  @MockBean
  var GDCRestClient: GDCRestClient = _
  @MockBean
  var applicationEventListener: ApplicationLifecycleEventListener = _
  @MockBean
  var gdcController: GdcController = _


  @Test
  def whenAllRequestsToADFSucceed_AndAllPipelinesSucceed_ThenIngestionSwitchSucceeds(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.StartingModeSwitch),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None
    )

    when(modeSwitchStateService.getIngestionModeSwitchState).thenReturn(initialModeSwitchState)
    when(modeSwitchStateService.getIngestionModeSwitchStartTime).thenReturn(Some(ingestionModeSwitchTimestamp))

    when(adfService.startCleanupPipeline()).thenReturn(Some(CLEANUP_PIPELINE_RUN_ID))
    when(adfService.isPipelineRunInProgress(CLEANUP_PIPELINE_RUN_ID)).thenReturn(false)
    when(adfService.isPipelineRunInFailedStatus(CLEANUP_PIPELINE_RUN_ID)).thenReturn(false)

    val mostRecentDayInThePast6AM = ADFService.computeMostRecentDayInThePast6AM(ingestionModeSwitchTimestamp)

    //Waiting for running pipelines

    //We assume the employee profiles pipeline run finished before we're checking for the first time
    when(adfService.getLatestPipelineRunsForTriggerStartingAfterTimestamp(
      ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER.triggerName,
      ingestionModeSwitchTimestamp,
      ADFTrigger.EMPLOYEE_PROFILES_PIPELINE_BACKFILL_TRIGGER.numOfPipelinesStartedByTrigger)).thenReturn(List("employee_run_id"))
    when(adfService.filterInProgressPipelines(List("employee_run_id"), mustFinishSuccessfully = true)).thenReturn(List())

    //We assume the emails pipeline runs are finished before we're checking for the first time
    when(adfService.getLatestPipelineRunsForTriggerStartingAfterTimestamp(
      ADFTrigger.EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER.triggerName,
      ingestionModeSwitchTimestamp,
      ADFTrigger.EMAILS_PIPELINE_BACKFILL_PAST_WEEK_TRIGGER.numOfPipelinesStartedByTrigger)).thenReturn(List("email_run_id1", "email_run_id2", "email_run_id3", "email_run_id4", "email_run_id5", "email_run_id6", "email_run_id7"))
    when(adfService.filterInProgressPipelines(List("email_run_id1", "email_run_id2", "email_run_id3", "email_run_id4", "email_run_id5", "email_run_id6", "email_run_id7"), mustFinishSuccessfully = true)).thenReturn(List())

    //We assume the inferred roles pipeline run finished before we're checking for the first time
    when(adfService.getLatestPipelineRunsForTriggerStartingAfterTimestamp(
      ADFTrigger.INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER.triggerName,
      ingestionModeSwitchTimestamp,
      ADFTrigger.INFERRED_ROLES_PIPELINE_BACKFILL_TRIGGER.numOfPipelinesStartedByTrigger)).thenReturn(List("inferred_roles_pipeline_id"))
    when(adfService.filterInProgressPipelines(List("inferred_roles_pipeline_id"), mustFinishSuccessfully = true)).thenReturn(List())

    //We assume the airtable pipeline run finished before we're checking for the first time
    when(adfService.getLatestPipelineRunsForTriggerStartingAfterTimestamp(
      ADFTrigger.AIRTABLE_PIPELINE_BACKFILL_TRIGGER.triggerName,
      ingestionModeSwitchTimestamp,
      ADFTrigger.AIRTABLE_PIPELINE_BACKFILL_TRIGGER.numOfPipelinesStartedByTrigger)).thenReturn(List("airtable_run_id"))
    when(adfService.filterInProgressPipelines(List("airtable_run_id"), mustFinishSuccessfully = true)).thenReturn(List())


    // ACT
    ingestionModeSwitchService.switchToIngestionMode(IngestionMode.Simulated, initialModeSwitchState, CORRELATION_ID)


    // ASSERT
    val webSocketPath = WebSocketService.INGESTION_MODE_SWITCH_STATE_CHANNEL_PATH

    verify(adfService).stopAllTriggers()
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.TriggersStopped))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, TriggersStopped))

    verify(adfService).deleteAllTriggers()
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.TriggersDeleted))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, TriggersDeleted))

    verify(adfService).cancelTriggerRunsFromLastMonth()
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.RunningTriggerRunsStopped))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, RunningTriggerRunsStopped))

    verify(adfService, Mockito.times(2)).cancelRunningPipelines()
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.RunningPipelinesStopped))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, RunningPipelinesStopped))

    verify(adfService).startCleanupPipeline()
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.CleanupPipelineStarted))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, CleanupPipelineStarted))

    verify(adfService).isPipelineRunInProgress(CLEANUP_PIPELINE_RUN_ID)
    verify(adfService).isPipelineRunInFailedStatus(CLEANUP_PIPELINE_RUN_ID)
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.CleanupPipelineCompleted))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, CleanupPipelineCompleted))

    verify(adfService).updateADFIngestionModeGlobalParameter(IngestionMode.Simulated)
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.IngestionModeUpdatedInAdf))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, IngestionModeUpdatedInAdf))

    verify(adfService).createAndStartTriggers(IngestionMode.Simulated, mostRecentDayInThePast6AM)
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.TriggersRecreated))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, TriggersRecreated))

    verify(adfService).createAndStartBackFillFurtherPastEmailTrigger(IngestionMode.Simulated, mostRecentDayInThePast6AM)
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.RecentDataBackfillPipelinesCompleted))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, RecentDataBackfillPipelinesCompleted))

    val finalModeSwitchState = initialModeSwitchState.copy(
      modeSwitchPhase = Some(IngestionModeSwitchPhase.Completed),
      modeSwitchRequester = None,
      modeSwitchStartTime = None
    )
    verify(modeSwitchStateService).setIngestionModeSwitchState(finalModeSwitchState)
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(finalModeSwitchState, Completed))

  }

  private def buildWebSocketMessage(state: IngestionModeSwitchState, phase: IngestionModeSwitchPhase): IngestionModeSwitchStateResponse = {
    IngestionModeSwitchStateResponse(state.cloneWithPhase(phase))
  }

  @Test
  def whenDeleteAdfTriggersCallFails_ThenIngestionSwitchProcessStops_AndIngestionPhaseIsSetToError(): Unit = {
    // ARRANGE
    val requester = "mockAdmin@mail.com"
    implicit val userToken: UserToken = UserToken("accessToken123", "refreshToken123", TokenScope.AZURE_MANAGEMENT_SCOPE)
    val ingestionModeSwitchTimestamp = ZonedDateTime.now(ZoneOffset.UTC)
    val errorMessage = "errorMessage"
    val exception = new DeleteTriggerFailureException(errorMessage)

    val initialModeSwitchState = IngestionModeSwitchState(
      ingestionMode = Some(IngestionMode.Simulated),
      modeSwitchPhase = Some(IngestionModeSwitchPhase.StartingModeSwitch),
      modeSwitchPaused = false,
      modeSwitchRequester = Some(requester),
      modeSwitchStartTime = Some(ingestionModeSwitchTimestamp),
      modeSwitchErrorMessage = None,
      modeSwitchErrorStackTrace = None,
      logsCorrelationId = None)


    when(adfService.deleteAllTriggers()).thenAnswer(new Answer[Unit] {
      override def answer(invocation: InvocationOnMock): Unit = throw exception
    })

    // ACT
    try {
      ingestionModeSwitchService.switchToIngestionMode(IngestionMode.Simulated, initialModeSwitchState, CORRELATION_ID)
    } catch {
      case e: Throwable =>
        assert(e.isInstanceOf[IngestionModeSwitchFailedException], "IngestionModeSwitchFailedException has to be thrown if on of the operations in switchToIngestionMode fails.")
    }

    // ASSERT
    val webSocketPath = WebSocketService.INGESTION_MODE_SWITCH_STATE_CHANNEL_PATH

    verify(adfService).stopAllTriggers()
    verify(modeSwitchStateService).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.TriggersStopped))
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, TriggersStopped))

    verify(modeSwitchStateService, Mockito.never()).setIngestionModeSwitchState(initialModeSwitchState.cloneWithPhase(IngestionModeSwitchPhase.TriggersDeleted))
    verify(webSocketService, Mockito.never()).emitNotification(webSocketPath, buildWebSocketMessage(initialModeSwitchState, TriggersDeleted))
    verify(adfService, Mockito.never()).cancelRunningPipelines()
    verify(adfService, Mockito.never()).cancelTriggerRunsFromLastMonth()

    val errorState = initialModeSwitchState.copy(
      modeSwitchPhase = Some(IngestionModeSwitchPhase.Error),
      modeSwitchErrorMessage = Some("Failed to delete trigger errorMessage"),
      modeSwitchErrorStackTrace = Some(ExceptionUtils.getStackTrace(exception)),
      logsCorrelationId = Some(CORRELATION_ID)
    )

    verify(modeSwitchStateService).setIngestionModeSwitchState(errorState)
    verify(webSocketService).emitNotification(webSocketPath, buildWebSocketMessage(errorState, Error))
  }


}

object IngestionModeSwitchServiceTest {

  var CLEANUP_PIPELINE_RUN_ID: String = "cleanupPipelineId"

  var CORRELATION_ID = "fb2ad26c-6a5a-4914-92a8-97b0f2367a71"

}
