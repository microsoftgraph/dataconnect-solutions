/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.response

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.microsoft.graphdataconnect.model.admin.IngestionMode.IngestionMode
import com.microsoft.graphdataconnect.model.admin.IngestionModeSwitchPhase.IngestionModeSwitchPhase
import com.microsoft.graphdataconnect.skillsfinder.config.serialization.{IngestionModeSwitchPhaseType, IngestionModeType}
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.IngestionModeSwitchState
import com.microsoft.graphdataconnect.utils.TimeUtils

case class IngestionModeSwitchStateResponse(
                                             @JsonScalaEnumeration(classOf[IngestionModeType])
                                             ingestionMode: IngestionMode,
                                             @JsonScalaEnumeration(classOf[IngestionModeSwitchPhaseType])
                                             modeSwitchPhase: IngestionModeSwitchPhase,
                                             modeSwitchPaused: Boolean,
                                             modeSwitchRequester: String,
                                             modeSwitchTimestamp: String,
                                             modeSwitchErrorMessage: String,
                                             modeSwitchErrorStackTrace: String,
                                             logsCorrelationId: String
                                           )

object IngestionModeSwitchStateResponse {
  def apply(details: IngestionModeSwitchState): IngestionModeSwitchStateResponse = {
    IngestionModeSwitchStateResponse(
      details.ingestionMode.orNull,
      details.modeSwitchPhase.orNull,
      details.modeSwitchPaused,
      details.modeSwitchRequester.orNull,
      details.modeSwitchStartTime.map(TimeUtils.zonedDateTimeToTimestampString(_)).orNull,
      details.modeSwitchErrorMessage.orNull,
      details.modeSwitchErrorStackTrace.orNull,
      details.logsCorrelationId.orNull
    )
  }
}
