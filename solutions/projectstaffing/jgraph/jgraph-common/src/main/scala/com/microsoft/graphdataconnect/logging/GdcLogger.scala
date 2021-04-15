package com.microsoft.graphdataconnect.logging

trait GdcLogger {
  def debug(message: String): Unit

  def info(message: String): Unit

  def error(message: String): Unit

  def error(message: String, e: Throwable): Unit
}
