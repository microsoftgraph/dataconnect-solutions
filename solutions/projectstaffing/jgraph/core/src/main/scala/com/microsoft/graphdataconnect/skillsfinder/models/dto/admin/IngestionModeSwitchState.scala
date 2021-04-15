package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import java.time.ZonedDateTime

import com.microsoft.graphdataconnect.model.admin.IngestionMode.IngestionMode
import com.microsoft.graphdataconnect.model.admin.IngestionModeSwitchPhase.IngestionModeSwitchPhase

case class IngestionModeSwitchState(
                                     ingestionMode: Option[IngestionMode],
                                     modeSwitchPhase: Option[IngestionModeSwitchPhase],
                                     modeSwitchPaused: Boolean,
                                     modeSwitchRequester: Option[String],
                                     modeSwitchStartTime: Option[ZonedDateTime],
                                     modeSwitchErrorMessage: Option[String],
                                     modeSwitchErrorStackTrace: Option[String],
                                     logsCorrelationId: Option[String] // Should only be set in case of mode switch failure
                                   ) {
  def cloneWithPhase(phase: IngestionModeSwitchPhase): IngestionModeSwitchState = {
    this.copy(modeSwitchPhase = Some(phase))
  }

  override def toString: String = {
    s"ingestionMode=${ingestionMode.orNull}, modeSwitchPhase=${modeSwitchPhase.orNull}, modeSwitchPaused=$modeSwitchPaused" +
      s"${printHelper("modeSwitchRequester", modeSwitchRequester)}" +
      s"${printHelper("modeSwitchTimestamp", modeSwitchStartTime)}" +
      s"${printHelper("logsCorrelationId", logsCorrelationId)}" +
      s"${printHelper("modeSwitchErrorMessage", modeSwitchErrorMessage)}"
  }

  private def printHelper(fieldName: String, field: Option[Any]): String = {
    field.map(value => s", $fieldName=$value").getOrElse("")
  }
}
