/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.logging

import org.slf4j.{Logger, LoggerFactory}

class Sl4jLogger(clazz: Class[_]) extends GdcLogger {
  val log: Logger = LoggerFactory.getLogger(clazz)

  def debug(message: String): Unit = {
    log.debug(message)
  }

  def info(message: String): Unit = {
    log.info(message)
  }

  def error(message: String): Unit = {
    log.error(message)
  }

  def error(message: String, e: Throwable): Unit = {
    log.error(message, e)
  }
}

