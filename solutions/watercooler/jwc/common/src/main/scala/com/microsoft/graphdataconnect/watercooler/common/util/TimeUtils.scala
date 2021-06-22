/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.util

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {

  private val localDateFormatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd", Locale.US)
  private val localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US)
  private val databaseDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
  private val azureLocalDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US) // 2021-02-02T17:00:00

  val oldestSqlDateStr: String = "1753-01-01 00:00:00.000000"
  val oldestSqlDate: LocalDateTime = LocalDateTime.parse(oldestSqlDateStr, localDateTimeFormatter)

  def stringToDate(dateStr: String): LocalDate = {
    LocalDate.parse(dateStr, localDateFormatter)
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

  def isAfterOrEqual(date1: LocalDate, date2: LocalDate): Boolean = {
    date1.isAfter(date2) || date1.isEqual(date2)
  }

  def isAfterOrEqual(date1: String, date2: String): Boolean = {
    isAfterOrEqual(stringToDate(date1), stringToDate(date2))
  }

  def timestampToLocalDateTime(timestampStr: String) : LocalDateTime = {
    LocalDateTime.parse(timestampStr, localDateTimeFormatter)
  }

  def databaseTimestampToLocalDateTime(timestampStr: String) : LocalDateTime = {
    LocalDateTime.parse(timestampStr, databaseDateTimeFormatter)
  }

  def localDateTimeToString(date: LocalDateTime): String = {
    date.format(localDateTimeFormatter)
  }

  def localDateTimeToAzureString(date: LocalDateTime): String = {
    date.format(azureLocalDateTimeFormatter)
  }

}
