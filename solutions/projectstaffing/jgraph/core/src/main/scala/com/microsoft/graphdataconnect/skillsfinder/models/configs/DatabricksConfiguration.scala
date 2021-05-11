/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.configs

import org.apache.commons.lang3.StringUtils

case class DatabricksConfiguration(
                                    address: String,
                                    apiToken: String,
                                    orgId: String,
                                    clusterId: String,
                                    port: String = "15001"
                                  ) {

  def validate(): Unit = {
    require(StringUtils.isNotBlank(address) && address.startsWith("https://"))
    require(StringUtils.isNotBlank(apiToken))
    require(StringUtils.isNotBlank(orgId))
    require(StringUtils.isNotBlank(clusterId))
  }
}
