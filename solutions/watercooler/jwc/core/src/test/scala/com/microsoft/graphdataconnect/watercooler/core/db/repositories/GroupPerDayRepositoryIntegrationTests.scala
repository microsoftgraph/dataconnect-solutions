/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.db.repositories

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.util

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerDay
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.GroupPerDayIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.NrOfGroupsPerDayPerHourInInterval
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.GroupPerDayRepository
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.core.setup.repository.AbstractIntegrationTestBase
import org.junit.{Assert, Test}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._

class GroupPerDayRepositoryIntegrationTests extends AbstractIntegrationTestBase {
  private val logger: Logger = LoggerFactory.getLogger(classOf[GroupPerDayRepositoryIntegrationTests])

  @Autowired
  var groupPerDayRepository: GroupPerDayRepository = _

  @Test
  def testGetGroupsOnHourOfDay(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val groupPerDay: GroupPerDay = new GroupPerDay()
    groupPerDay.composedId = new GroupPerDayIdentity("1", version)
    groupPerDay.hourTimeSlot = roundHourNow
    // mandatory fields
    groupPerDay.day = roundHourNow
    groupPerDay.displayName = "G1"
    groupPerDay.groupName = "g1"
    groupPerDay.groupMembers = ""
    groupPerDay.timezoneNr = "00"
    groupPerDay.timezoneStr = "UTC"

    groupPerDayRepository.save(groupPerDay)

    // act
    val groups: List[GroupPerDay] = groupPerDayRepository.getGroupsOnHourOfDay(roundHourNow, version).asScala.toList

    // assert
    Assert.assertTrue("groups should not be null", groups != null)
    Assert.assertTrue("groups should not be empty", groups.nonEmpty)
    Assert.assertTrue("groups should have one row", groups.size == 1)
  }

  @Test
  def testGetGroupsOnDay(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val dayNow = LocalDate.now()
    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val groupPerDay: GroupPerDay = new GroupPerDay()
    groupPerDay.composedId = new GroupPerDayIdentity("3", version)
    groupPerDay.hourTimeSlot = roundHourNow
    // mandatory fields
    groupPerDay.day = roundHourNow
    groupPerDay.displayName = "G1"
    groupPerDay.groupName = "g1"
    groupPerDay.groupMembers = ""
    groupPerDay.timezoneNr = "00"
    groupPerDay.timezoneStr = "UTC"

    groupPerDayRepository.save(groupPerDay)

    // act
    val groups: List[GroupPerDay] = groupPerDayRepository.getGroupsOnDay(dayNow, 0, filterTimezone, version).asScala.toList

    // assert
    Assert.assertTrue("groups should not be null", groups != null)
    Assert.assertTrue("groups should not be empty", groups.nonEmpty)
    Assert.assertTrue("groups should have one row", groups.size == 1)
  }

  @Test
  def testGetGroupsOnDay_timezone(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val dayNow = LocalDate.now()
    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val groupPerDay: GroupPerDay = new GroupPerDay()
    groupPerDay.composedId = new GroupPerDayIdentity("4", version)
    groupPerDay.hourTimeSlot = roundHourNow
    // mandatory fields
    groupPerDay.day = roundHourNow
    groupPerDay.displayName = "G1"
    groupPerDay.groupName = "g1"
    groupPerDay.groupMembers = ""
    groupPerDay.timezoneNr = "00"
    groupPerDay.timezoneStr = "UTC"

    groupPerDayRepository.save(groupPerDay)

    // act
    val groups: List[GroupPerDay] = groupPerDayRepository.getGroupsOnDay(dayNow, 1, filterTimezone, version).asScala.toList

    // assert
    Assert.assertTrue("groups should not be null", groups != null)
    Assert.assertTrue("groups should not be empty", groups.nonEmpty)
    Assert.assertTrue("groups should have one row", groups.size == 1)
  }


  @Test
  def testComputeNumbersOfGroupsPerHourPerDayInWeek(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val dayNow = LocalDate.now()
    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val groupPerDay: GroupPerDay = new GroupPerDay()
    groupPerDay.composedId = new GroupPerDayIdentity("4", version)
    groupPerDay.hourTimeSlot = roundHourNow
    // mandatory fields
    groupPerDay.day = roundHourNow
    groupPerDay.displayName = "G1"
    groupPerDay.groupName = "g1"
    groupPerDay.groupMembers = ""
    groupPerDay.timezoneNr = "00"
    groupPerDay.timezoneStr = "UTC"

    groupPerDayRepository.save(groupPerDay)

    val dayBefore = roundHourNow.minus(1, ChronoUnit.DAYS).toLocalDate
    val dayAfter = roundHourNow.plus(1, ChronoUnit.DAYS).toLocalDate

    val groups: List[NrOfGroupsPerDayPerHourInInterval] = groupPerDayRepository.computeNumberOfGroupsPerDayPerHour(dayBefore,dayAfter, -8, filterTimezone, version).asScala.toList

    // assert
    Assert.assertTrue("groups should not be null", groups != null)
    Assert.assertTrue("groups should not be empty", groups.nonEmpty)
    Assert.assertTrue("groups should have one row", groups.size == 1)
  }



}
