/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.config

import com.microsoft.graphdataconnect.model.admin.{HRDataIngestionPhase, IngestionModeSwitchPhase}
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.{HRDataIngestionStateModel, HRDataIngestionStateResponse}
import com.microsoft.graphdataconnect.skillsfinder.models.response
import com.microsoft.graphdataconnect.skillsfinder.service.ingestionmode.{IngestionModeSwitchService, ModeSwitchStateService}
import com.microsoft.graphdataconnect.skillsfinder.service.{HRDataIngestionService, HRDataIngestionStateService, WebSocketService}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ApplicationLifecycleEventListener(@Autowired val modeSwitchStateService: ModeSwitchStateService,
                                        @Autowired val ingestionModeSwitchService: IngestionModeSwitchService,
                                        @Autowired val hrDataIngestionService: HRDataIngestionService,
                                        @Autowired val hrDataIngestionStateService: HRDataIngestionStateService,
                                        @Autowired val webSocketService: WebSocketService) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[ApplicationLifecycleEventListener])

  @EventListener
  def onApplicationReadyEvent(applicationReadyEvent: ApplicationReadyEvent): Unit = {
    //    val initialModeSwitchState = IngestionModeSwitchState(
    //      ingestionMode = Some(IngestionMode.Sample),
    //      modeSwitchPhase = Some(IngestionModeSwitchPhase.TriggersRecreated),
    //      modeSwitchPaused = false,
    //      modeSwitchRequester = Some("demoUser@example.com"),
    //      modeSwitchStartTime = Some(ZonedDateTime.now(ZoneOffset.UTC)),
    //      modeSwitchErrorMessage = None,
    //      modeSwitchErrorStackTrace = None,
    //      logsCorrelationId = None
    //    )
    //    modeSwitchStateService.setIngestionModeSwitchState(initialModeSwitchState)

    // Handle ingestion mode switch that was ongoing during application restart
    val currentIngestionModeSwitchState = modeSwitchStateService.getIngestionModeSwitchState
    val currentIngestionModeSwitchPhase: Option[IngestionModeSwitchPhase.Value] = currentIngestionModeSwitchState.modeSwitchPhase

    logger.info(s"Application status at startup: $currentIngestionModeSwitchState")

    if (currentIngestionModeSwitchPhase.isDefined &&
      currentIngestionModeSwitchPhase.get != IngestionModeSwitchPhase.Completed &&
      currentIngestionModeSwitchPhase.get != IngestionModeSwitchPhase.Error) {
      logger.info(s"Ingestion mode switch process is not completed and got paused by application restart. It requires admin intervention to resume")
      val modeSwitchPausedState = currentIngestionModeSwitchState.copy(modeSwitchPaused = true)

      modeSwitchStateService.setIngestionModeSwitchState(modeSwitchPausedState)
      // Pushing notifications to UI via websockets is probably futile, as the connection was broken during app restart,
      // but we do this anyway in case a browser (re)connected in the short time-span required for this code to execute
      webSocketService.emitNotification(WebSocketService.INGESTION_MODE_SWITCH_STATE_CHANNEL_PATH, response.IngestionModeSwitchStateResponse(modeSwitchPausedState))
      logger.info(s"Stored in DB and notified listeners about new ingestion mode switch state: $modeSwitchPausedState")

      //TODO Notify the admin via Email if ingestion switch process should resume
    }


    // Handle HR Data file ingestion that was ongoing during application restart
    val currentHRDataIngestionStateOpt: Option[HRDataIngestionStateModel] = hrDataIngestionStateService.getHRDataIngestionState()
    if (currentHRDataIngestionStateOpt.isDefined) {
      val currentHRDataIngestionState = currentHRDataIngestionStateOpt.get
      logger.info(s"HR Data ingestion state ar startup: $currentHRDataIngestionState")

      if (currentHRDataIngestionState.phase != HRDataIngestionPhase.Error && currentHRDataIngestionState.phase != HRDataIngestionPhase.EmployeePipelineRunFinished) {
        // Checking the state of ADF employees pipelines is not possible at this point as that would require an active user
        // whom to impersonate. There is also the risk that the app restarted before it managed to finish uploading a large file,
        // so proceeding with ADF monitoring might not have been enough anyway. For now we are going to mark the HR Data
        // ingestion process as failed, to ensure it does not remain blocked in running state and thus prevent further uploads.
        logger.info(s"HR Data ingestion process is not completed and got paused by application restart. Marking it as failed so that upload can be retried by an admin")
        val failedHRDataIngestionState = currentHRDataIngestionState.copy(phase = HRDataIngestionPhase.Error,
          errorMessage = "HR Data ingestion process failed due to application restart")

        hrDataIngestionStateService.saveHRDataIngestionState(failedHRDataIngestionState)
        // Pushing notifications to UI via websockets is probably futile, as the connection was broken during app restart,
        // but we do this anyway in case a browser (re)connected in the short time-span required for this code to execute
        webSocketService.emitNotification(WebSocketService.INGEST_HR_DATA_STATE_CHANNEL_PATH, HRDataIngestionStateResponse(failedHRDataIngestionState.phase))
        logger.info(s"Stored in DB and notified listeners about new HR Data ingestion state: $failedHRDataIngestionState")
      }
    }

  }

}
