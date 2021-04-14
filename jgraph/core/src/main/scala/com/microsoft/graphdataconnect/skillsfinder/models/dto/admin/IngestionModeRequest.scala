package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.microsoft.graphdataconnect.model.admin.IngestionMode
import com.microsoft.graphdataconnect.skillsfinder.config.serialization.IngestionModeType

case class IngestionModeRequest(@JsonScalaEnumeration(classOf[IngestionModeType])
                                mode: IngestionMode.IngestionMode)
