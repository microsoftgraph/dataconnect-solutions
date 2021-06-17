/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator.config

case class ConfigFile(applicationId: String = "",
                      applicationSecret: String = "",
                      storageAccountName: String = "",
                      directoryId: String = "",
                      meetingOrganizerEmail: String = "",
                      storageAccount: String = "",
                      storageSecret: String = "",
                      outputFolderPath: String = "",
                      outputContainer: String = "",
                      maxDbConnections: String = "",
                      jdbcHostname: String = "",
                      jdbcPort: String = "",
                      jdbcDatabase: String = "",
                      jdbcUsername: String = "",
                      jdbcPassword: String = "",
                      startDate: String = "", // yyyy-MM-dd format
                      endDate: String = "",  // yyyy-MM-dd format
                      meetingDuration: String = ""
                     ) {

  private def validate(): Unit = {

  }

}
