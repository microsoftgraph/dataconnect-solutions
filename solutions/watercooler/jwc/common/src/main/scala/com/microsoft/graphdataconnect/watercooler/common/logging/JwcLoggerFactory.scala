/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.logging

object JwcLoggerFactory {
  def getLogger(logAnalyticsWorkspaceId: String = "", //"b61e5e81-9eb2-413e-aaef-624b89af04a0"
                //Use either the primary or the secondary client authentication key
                logAnalyticsSharedKey: String = "",
                clazz: Class[_],
                logType: String = "GDC_Pipelines",
                logServerTime: Boolean = true): JwcLogger = {
    if (!logAnalyticsWorkspaceId.trim.isEmpty && !logAnalyticsSharedKey.trim.isEmpty) {
      new LogAnalyticsLogger(workspaceId = logAnalyticsWorkspaceId,
        sharedKey = logAnalyticsSharedKey,
        clazz = clazz,
        logType = logType,
        logServerTime = logServerTime
      )
    } else {
      new Sl4jLogger(clazz)
    }
  }
}
