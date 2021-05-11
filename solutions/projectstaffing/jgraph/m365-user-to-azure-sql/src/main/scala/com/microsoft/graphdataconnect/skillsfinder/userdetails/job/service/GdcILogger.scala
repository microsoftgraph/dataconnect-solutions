/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.service

import com.microsoft.graph.logger.{ILogger, LoggerLevel}
import com.microsoft.graphdataconnect.logging.GdcLogger

class GdcILogger(val gdcLogger: GdcLogger) extends ILogger {
  var loggerLevel: LoggerLevel = LoggerLevel.ERROR

  override def setLoggingLevel(level: LoggerLevel): Unit = {
    loggerLevel = level
  }

  override def getLoggingLevel: LoggerLevel = loggerLevel

  override def logDebug(message: String): Unit = if (loggerLevel.equals(LoggerLevel.DEBUG)) gdcLogger.debug(message)

  override def logError(message: String, throwable: Throwable): Unit = gdcLogger.error(message, throwable)
}
