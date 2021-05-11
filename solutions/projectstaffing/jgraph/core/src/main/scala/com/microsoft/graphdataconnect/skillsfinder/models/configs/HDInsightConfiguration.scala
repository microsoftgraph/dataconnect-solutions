/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.configs

import org.apache.commons.lang3.StringUtils

case class HDInsightConfiguration(
                                   clusterId: String,
                                   clusterName: String,
                                   address: String,
                                   user: String,
                                   password: String
                                 ) {

  def validate(): Unit = {
    require(StringUtils.isNotBlank(clusterId))
    require(StringUtils.isNotBlank(clusterName))
    require(StringUtils.isNotBlank(address) && address.startsWith("https://"))
    require(StringUtils.isNotBlank(user))
    require(StringUtils.isNotBlank(password))
  }
}
