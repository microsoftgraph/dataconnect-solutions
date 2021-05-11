/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.utils

import java.time._
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {

  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
  private val localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US)
  private val zonedDateTimeFormatter = DateTimeFormatter.ISO_INSTANT

  val oldestSqlDate: LocalDateTime = LocalDateTime.parse("1753-01-01 00:00:00.000000", localDateTimeFormatter)
  val oldestSqlDateZDT: ZonedDateTime = ZonedDateTime.of(oldestSqlDate, ZoneOffset.UTC)


  def stringToDate(dateStr: String): LocalDate = {
    LocalDate.parse(dateStr, localDateFormatter) // seconds in a day
  }

  def dateToString(date: LocalDate): String = {
    localDateFormatter.format(date)
  }

  def isBeforeOrEqual(date1: LocalDate, date2: LocalDate): Boolean = {
    date1.isBefore(date2) || date1.isEqual(date2)
  }

  def isBeforeOrEqual(date1: String, date2: String): Boolean = {
    isBeforeOrEqual(stringToDate(date1), stringToDate(date2))
  }

  def timestampToLocalDateTime(timestampStr: String): LocalDateTime = {
    LocalDateTime.parse(timestampStr, localDateTimeFormatter)
  }

  def localDateTimeToString(date: LocalDateTime): String = {
    date.format(localDateTimeFormatter)
  }

  def timestampStringToZonedDateTime(timestampStr: String): ZonedDateTime = {
    def instant = Instant.from(zonedDateTimeFormatter.parse(timestampStr))

    ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
  }

  def zonedDateTimeToTimestampString(zonedDateTime: ZonedDateTime): String = {
    zonedDateTime.format(zonedDateTimeFormatter)
  }

}
