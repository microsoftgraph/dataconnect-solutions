/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.microsoft.graphdataconnect.model.admin.IngestionMode
import com.microsoft.graphdataconnect.skillsfinder.config.serialization.IngestionModeType

case class IngestionModeResponse(
                                  @JsonScalaEnumeration(classOf[IngestionModeType])
                                  mode: IngestionMode.IngestionMode,
                                  permissionConsentLink: String = ""
                                )

object IngestionModeResponse {
  def apply(ingestionModeRequest: IngestionModeRequest): IngestionModeResponse = {
    IngestionModeResponse(mode = ingestionModeRequest.mode)
  }
}

