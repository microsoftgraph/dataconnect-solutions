/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.services

import java.time.{ZoneId, ZoneOffset, ZonedDateTime}

import com.microsoft.graphdataconnect.skillsfinder.service.adf.ADFService
import org.junit.Test

class MostRecentDayInThePast6AMComputationTest {

  @Test
  def whenIngestionModeSwitchTimestamp_IsBefore6AMUTC_ThenResultIs6AMYesterday(): Unit = {
    val ingestionModeSwitchTimestampUTC = ZonedDateTime.of(2001, 1, 21,
      5, 10, 10, 10, ZoneOffset.UTC)
    val mostRecentDayInThePast6AMUTC = ADFService.computeMostRecentDayInThePast6AM(ingestionModeSwitchTimestamp = ingestionModeSwitchTimestampUTC)

    val sixAMYesterday = ZonedDateTime.of(2001, 1, 20,
      6, 0, 0, 0, ZoneOffset.UTC)

    assert(mostRecentDayInThePast6AMUTC.equals(sixAMYesterday))
  }

  @Test
  def whenIngestionModeSwitchTimestamp_IsAfter6AMUTC_ThenResultIs6AMToday(): Unit = {
    val ingestionModeSwitchTimestampUTC = ZonedDateTime.of(2001, 1, 21,
      7, 10, 10, 10, ZoneOffset.UTC)
    val mostRecentDayInThePast6AMUTC = ADFService.computeMostRecentDayInThePast6AM(ingestionModeSwitchTimestamp = ingestionModeSwitchTimestampUTC)

    val sixAMToday = ZonedDateTime.of(2001, 1, 21,
      6, 0, 0, 0, ZoneOffset.UTC)

    assert(mostRecentDayInThePast6AMUTC.equals(sixAMToday))
  }

  @Test
  def whenIngestionModeSwitchTimestamp_IsBefore6AMUTCAndAfter6AMLocal_ThenResultIs6AMYesterday(): Unit = {
    val ingestionModeSwitchTimestampLocal = ZonedDateTime.of(2001, 1, 21,
      6, 10, 10, 10, ZoneId.of("Europe/Paris")) // 05:10 in UTC
    val mostRecentDayInThePast6AMLocal = ADFService.computeMostRecentDayInThePast6AM(ingestionModeSwitchTimestamp = ingestionModeSwitchTimestampLocal)

    val sixAMYesterday = ZonedDateTime.of(2001, 1, 20,
      6, 0, 0, 0, ZoneOffset.UTC)

    assert(mostRecentDayInThePast6AMLocal.equals(sixAMYesterday))
  }

  @Test
  def whenIngestionModeSwitchTimestamp_IsAfter6AMTomorrowUTCAndAfter6AMLocal_ThenResultIs6AMTomorrowUTC(): Unit = {
    val ingestionModeSwitchTimestampLocal = ZonedDateTime.of(2001, 1, 21,
      22, 10, 10, 10, ZoneId.of("America/Los_Angeles")) // 06:10 AM next day in UTC
    val mostRecentDayInThePast6AMLocal = ADFService.computeMostRecentDayInThePast6AM(ingestionModeSwitchTimestamp = ingestionModeSwitchTimestampLocal)

    val sixAMTomorrowUTC = ZonedDateTime.of(2001, 1, 22,
      6, 0, 0, 0, ZoneOffset.UTC)

    assert(mostRecentDayInThePast6AMLocal.equals(sixAMTomorrowUTC))
  }

  @Test
  def whenIngestionModeSwitchTimestamp_IsAfter6AMUTCAndBefore6AMLocal_ThenResultIs6AMToday(): Unit = {
    val ingestionModeSwitchTimestampLocal = ZonedDateTime.of(2001, 1, 21,
      2, 10, 10, 10, ZoneId.of("America/New_York")) // 07:10 AM same day in UTC
    val mostRecentDayInThePast6AMLocal = ADFService.computeMostRecentDayInThePast6AM(ingestionModeSwitchTimestamp = ingestionModeSwitchTimestampLocal)

    val sixAMToday = ZonedDateTime.of(2001, 1, 21,
      6, 0, 0, 0, ZoneOffset.UTC)

    assert(mostRecentDayInThePast6AMLocal.equals(sixAMToday))
  }

}
