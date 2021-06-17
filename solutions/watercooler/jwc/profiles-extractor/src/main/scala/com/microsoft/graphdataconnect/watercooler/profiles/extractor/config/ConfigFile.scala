/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */


package com.microsoft.graphdataconnect.watercooler.profiles.extractor.config

case class ConfigFile(applicationId: String = "",
                      applicationSecret: String = "",
                      directoryId: String = "",
                      maxDbConnections: String = "",
                      jdbcHostname: String = "",
                      jdbcPort: String = "",
                      jdbcDatabase: String = "",
                      jdbcUsername: String = "",
                      jdbcPassword: String = "",
                      ) {

  private def validate(): Unit = {

  }

}
