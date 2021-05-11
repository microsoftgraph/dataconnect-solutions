/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.logging

object GdcLoggerFactory {
  def getLogger(logAnalyticsWorkspaceId: String = "",
                //Use either the primary or the secondary client authentication key
                logAnalyticsSharedKey: String = "",
                clazz: Class[_],
                logType: String = "GDC_Pipelines",
                logServerTime: Boolean = true): GdcLogger = {
    if (null != logAnalyticsWorkspaceId && !logAnalyticsWorkspaceId.trim.isEmpty &&
      null != logAnalyticsSharedKey && !logAnalyticsSharedKey.trim.isEmpty) {
      new LogAnalyticsLogger(workspaceId = logAnalyticsWorkspaceId.trim,
        sharedKey = logAnalyticsSharedKey.trim,
        clazz = clazz,
        logType = logType,
        logServerTime = logServerTime
      )
    } else {
      new Sl4jLogger(clazz)
    }
  }
}
