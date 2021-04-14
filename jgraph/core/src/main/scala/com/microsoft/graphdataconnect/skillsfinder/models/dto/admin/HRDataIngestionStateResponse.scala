package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.microsoft.graphdataconnect.model.admin.HRDataIngestionPhase.HRDataIngestionPhase
import com.microsoft.graphdataconnect.skillsfinder.config.serialization.HRDataIngestionPhaseType

case class HRDataIngestionStateResponse(@JsonScalaEnumeration(classOf[HRDataIngestionPhaseType])
                                        hrDataIngestionState: HRDataIngestionPhase)
