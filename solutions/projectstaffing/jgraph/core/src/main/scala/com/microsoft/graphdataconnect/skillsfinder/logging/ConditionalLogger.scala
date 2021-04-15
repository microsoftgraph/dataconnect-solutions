package com.microsoft.graphdataconnect.skillsfinder.logging

import org.slf4j.{Logger, LoggerFactory, Marker}

class ConditionalLogger(enableLogging: Boolean) {

  private val log: Logger = LoggerFactory.getLogger(classOf[ConditionalLogger])

  def getName: String = log.getName

  def isTraceEnabled: Boolean = log.isTraceEnabled

  def trace(s: String): Unit = if (enableLogging) log.trace(s)

  def trace(s: String, o: Any): Unit = if (enableLogging) log.trace(s, o)

  def trace(s: String, o: Any, o1: Any): Unit = if (enableLogging) log.trace(s, o, o1)

  def trace(s: String, objects: Any*): Unit = if (enableLogging) log.trace(s, objects)

  def trace(s: String, throwable: Throwable): Unit = if (enableLogging) log.trace(s, throwable)

  def isTraceEnabled(marker: Marker): Boolean = log.isTraceEnabled(marker)

  def trace(marker: Marker, s: String): Unit = if (enableLogging) log.trace(marker, s)

  def trace(marker: Marker, s: String, o: Any): Unit = if (enableLogging) log.trace(marker, s, o)

  def trace(marker: Marker, s: String, o: Any, o1: Any): Unit = if (enableLogging) log.trace(marker, s, o, o1)

  def trace(marker: Marker, s: String, objects: Any*): Unit = if (enableLogging) log.trace(marker, s, objects)

  def trace(marker: Marker, s: String, throwable: Throwable): Unit = if (enableLogging) log.trace(marker, s, throwable)

  def isDebugEnabled: Boolean = log.isDebugEnabled

  def debug(s: String): Unit = if (enableLogging) log.debug(s)

  def debug(s: String, o: Any): Unit = if (enableLogging) log.debug(s, o)

  def debug(s: String, o: Any, o1: Any): Unit = if (enableLogging) log.debug(s, o, o1)

  def debug(s: String, objects: Any*): Unit = if (enableLogging) log.debug(s, objects)

  def debug(s: String, throwable: Throwable): Unit = if (enableLogging) log.debug(s, throwable)

  def isDebugEnabled(marker: Marker): Boolean = log.isDebugEnabled(marker)

  def debug(marker: Marker, s: String): Unit = if (enableLogging) log.debug(marker, s)

  def debug(marker: Marker, s: String, o: Any): Unit = if (enableLogging) log.debug(marker, s, o)

  def debug(marker: Marker, s: String, o: Any, o1: Any): Unit = if (enableLogging) log.debug(marker, s, o, o1)

  def debug(marker: Marker, s: String, objects: Any*): Unit = if (enableLogging) log.debug(marker, s, objects)

  def debug(marker: Marker, s: String, throwable: Throwable): Unit = if (enableLogging) log.debug(marker, s, throwable)

  def isInfoEnabled: Boolean = log.isInfoEnabled

  def info(s: String): Unit = if (enableLogging) log.info(s)

  def info(s: String, o: Any): Unit = if (enableLogging) log.info(s, o)

  def info(s: String, o: Any, o1: Any): Unit = if (enableLogging) log.info(s, o, o1)

  def info(s: String, objects: Any*): Unit = if (enableLogging) log.info(s, objects)

  def info(s: String, throwable: Throwable): Unit = if (enableLogging) log.info(s, throwable)

  def isInfoEnabled(marker: Marker): Boolean = log.isInfoEnabled(marker)

  def info(marker: Marker, s: String): Unit = if (enableLogging) log.info(marker, s)

  def info(marker: Marker, s: String, o: Any): Unit = if (enableLogging) log.info(marker, s, o)

  def info(marker: Marker, s: String, o: Any, o1: Any): Unit = if (enableLogging) log.info(marker, s, o, o1)

  def info(marker: Marker, s: String, objects: Any*): Unit = if (enableLogging) log.info(marker, s, objects)

  def info(marker: Marker, s: String, throwable: Throwable): Unit = if (enableLogging) log.info(marker, s, throwable)

  def isWarnEnabled: Boolean = log.isWarnEnabled

  def warn(s: String): Unit = if (enableLogging) log.warn(s)

  def warn(s: String, o: Any): Unit = if (enableLogging) log.warn(s, o)

  def warn(s: String, objects: Any*): Unit = if (enableLogging) log.warn(s, objects)

  def warn(s: String, o: Any, o1: Any): Unit = if (enableLogging) log.warn(s, o, o1)

  def warn(s: String, throwable: Throwable): Unit = if (enableLogging) log.warn(s, throwable)

  def isWarnEnabled(marker: Marker): Boolean = log.isWarnEnabled(marker)

  def warn(marker: Marker, s: String): Unit = if (enableLogging) log.warn(marker, s)

  def warn(marker: Marker, s: String, o: Any): Unit = if (enableLogging) log.warn(marker, s, o)

  def warn(marker: Marker, s: String, o: Any, o1: Any): Unit = if (enableLogging) log.warn(marker, s, o, o1)

  def warn(marker: Marker, s: String, objects: Any*): Unit = if (enableLogging) log.warn(marker, s, objects)

  def warn(marker: Marker, s: String, throwable: Throwable): Unit = if (enableLogging) log.warn(marker, s, throwable)

  def isErrorEnabled: Boolean = log.isErrorEnabled

  def error(s: String): Unit = if (enableLogging) log.error(s)

  def error(s: String, o: Any): Unit = if (enableLogging) log.error(s, o)

  def error(s: String, o: Any, o1: Any): Unit = if (enableLogging) log.error(s, o, o1)

  def error(s: String, throwable: Throwable): Unit = if (enableLogging) log.error(s, throwable)

  def isErrorEnabled(marker: Marker): Boolean = log.isErrorEnabled(marker)

  def error(marker: Marker, s: String): Unit = if (enableLogging) log.error(marker, s)

  def error(marker: Marker, s: String, o: Any): Unit = if (enableLogging) log.error(marker, s, o)

  def error(marker: Marker, s: String, o: Any, o1: Any): Unit = if (enableLogging) log.error(marker, s, o, o1)

  def error(marker: Marker, s: String, objects: Any*): Unit = if (enableLogging) log.error(marker, s, objects)

  def error(marker: Marker, s: String, throwable: Throwable): Unit = if (enableLogging) log.error(marker, s, throwable)

}
