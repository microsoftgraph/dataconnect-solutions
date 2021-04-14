package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.microsoft.graphdataconnect.model.admin.IngestionModeSwitchPhase
import com.microsoft.graphdataconnect.skillsfinder.config.serialization.IngestionModeSwitchPhaseType

case class IngestionModeSwitchPhaseDTO(
                                        @JsonScalaEnumeration(classOf[IngestionModeSwitchPhaseType])
                                        phase: IngestionModeSwitchPhase.IngestionModeSwitchPhase
                                      )

