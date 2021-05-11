/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.logging

trait GdcLogger {
  def debug(message: String): Unit

  def info(message: String): Unit

  def error(message: String): Unit

  def error(message: String, e: Throwable): Unit
}
