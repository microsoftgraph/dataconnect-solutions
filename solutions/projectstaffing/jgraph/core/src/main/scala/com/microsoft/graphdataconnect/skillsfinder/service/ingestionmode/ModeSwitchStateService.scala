/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service.ingestionmode

import java.time.ZonedDateTime

import com.microsoft.graphdataconnect.model.admin.IngestionModeSwitchPhase.IngestionModeSwitchPhase
import com.microsoft.graphdataconnect.model.admin.{IngestionMode, IngestionModeSwitchPhase}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.ModeSwitchState
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.IngestionModeSwitchStateRepository
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.IngestionModeSwitchState
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ModeSwitchStateService(@Autowired val modeSwitchStateRepository: IngestionModeSwitchStateRepository) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[ModeSwitchStateService])

  def getIngestionModeSwitchState: IngestionModeSwitchState = {
    Option(modeSwitchStateRepository.findFirstByOrderByIdDesc().orElse(null))
      .map(modeSwitchState =>
        IngestionModeSwitchState(
          Option(modeSwitchState.ingestionMode).flatMap(IngestionMode.withCaseInsensitiveName),
          Option(modeSwitchState.phase).flatMap(IngestionModeSwitchPhase.withCaseInsensitiveName),
          modeSwitchState.paused,
          Option(modeSwitchState.requester),
          Option(modeSwitchState.startTime).map(TimeUtils.timestampStringToZonedDateTime),
          Option(modeSwitchState.errorMessage),
          Option(modeSwitchState.errorStackTrace),
          Option(modeSwitchState.logsCorrelationId))
      )
      .getOrElse(IngestionModeSwitchState(None, None, false, None, None, None, None, None))
  }

  def setIngestionModeSwitchState(state: IngestionModeSwitchState): Unit = {
    val modeSwitchState = new ModeSwitchState()
    modeSwitchState.ingestionMode = state.ingestionMode.map(_.toString).orNull
    modeSwitchState.phase = state.modeSwitchPhase.map(_.toString).orNull
    modeSwitchState.paused = state.modeSwitchPaused
    modeSwitchState.requester = state.modeSwitchRequester.orNull
    modeSwitchState.startTime = state.modeSwitchStartTime.map(TimeUtils.zonedDateTimeToTimestampString).orNull
    modeSwitchState.errorMessage = state.modeSwitchErrorMessage.orNull
    modeSwitchState.errorStackTrace = state.modeSwitchErrorStackTrace.orNull
    modeSwitchState.logsCorrelationId = state.logsCorrelationId.orNull

    logger.debug(s"Setting mode switch state in DB to $modeSwitchState")
    modeSwitchStateRepository.save(modeSwitchState)
  }

  def getIngestionMode: Option[IngestionMode.Value] = {
    getIngestionModeSwitchState.ingestionMode
  }

  def getIngestionModeSwitchPhase: Option[IngestionModeSwitchPhase] = {
    getIngestionModeSwitchState.modeSwitchPhase
  }

  def getIngestionModeSwitchRequester: Option[String] = {
    getIngestionModeSwitchState.modeSwitchRequester
  }

  def isIngestionModeSwitchPaused: Boolean = {
    getIngestionModeSwitchState.modeSwitchPaused
  }

  def getIngestionModeSwitchStartTime: Option[ZonedDateTime] = {
    getIngestionModeSwitchState.modeSwitchStartTime
  }

  def getIngestionModeSwitchErrorMessage: Option[String] = {
    getIngestionModeSwitchState.modeSwitchErrorMessage
  }

  def getIngestionModeSwitchErrorStackTrace: Option[String] = {
    getIngestionModeSwitchState.modeSwitchErrorStackTrace
  }

  def getLogsCorrelationId: Option[String] = {
    getIngestionModeSwitchState.logsCorrelationId
  }
}
