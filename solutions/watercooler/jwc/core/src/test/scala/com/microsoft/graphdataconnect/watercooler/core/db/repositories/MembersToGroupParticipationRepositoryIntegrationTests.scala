/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.db.repositories

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.util

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics._
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.MembersToGroupParticipation
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.MembersToGroupParticipationIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.{AvgGroupCountsInInterval, AvgMinMaxGroupSize, AvgParticipationPerGroupPerDayInInterval, ParticipationStatusCounts, ParticipationStatusGroupedByDayResults, SelectedStatusCounts, SelectedStatusGroupedByDayResults}
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.MembersToGroupParticipationRepository
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.core.setup.repository.AbstractIntegrationTestBase
import org.junit.{Assert, Test}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._

class MembersToGroupParticipationRepositoryIntegrationTests extends AbstractIntegrationTestBase {
  private val logger: Logger = LoggerFactory.getLogger(classOf[MembersToGroupParticipationRepositoryIntegrationTests])

  @Autowired
  var membersToGroupParticipationRepository: MembersToGroupParticipationRepository = _


  @Test
  def testFindFirstByGroupNameAndMemberEmailAndComposedIdVersion(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g1"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 1
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    // act
    val group: Option[MembersToGroupParticipation] = Option(membersToGroupParticipationRepository.findFirstByGroupNameAndMemberEmailAndComposedIdVersion("g1", "a@a.a", version).orElse(null))

    // assert
    Assert.assertTrue("group should not be empty", group.nonEmpty)
    Assert.assertTrue("group id should match", membersToGroupParticipation.composedId == group.get.composedId)
  }

  @Test
  def testFindByGroupNameInAndComposedIdVersion(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g1"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 1
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    val nameList = new util.ArrayList[String]()
    nameList.add("g1")
    // act
    val groups: List[MembersToGroupParticipation] = membersToGroupParticipationRepository.findByGroupNameInAndComposedIdVersion(nameList, version).asScala.toList

    // assert
    Assert.assertTrue("MembersToGroupParticipation should not be null", groups != null)
    Assert.assertTrue("MembersToGroupParticipation should not be empty", groups.nonEmpty)
    Assert.assertTrue("MembersToGroupParticipation should have 1 row", groups.size == 1)
  }

  @Test
  def testCountByParticipationStatus(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g2"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 1
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    // act
    val counts: List[ParticipationStatusCounts] = membersToGroupParticipationRepository.countByParticipationStatus(version, filterTimezone).asScala.toList

    // assert
    Assert.assertTrue("MembersToGroupParticipation should not be null", counts != null)
    Assert.assertTrue("MembersToGroupParticipation should not be empty", counts.nonEmpty)
    Assert.assertTrue("MembersToGroupParticipation should have 1 row", counts.size == 1)
  }

  @Test
  def testCountBySelectedStatus(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g2"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 1
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    // act
    val counts: List[SelectedStatusCounts] = membersToGroupParticipationRepository.countBySelectedStatus(version, filterTimezone).asScala.toList

    // assert
    Assert.assertTrue("MembersToGroupParticipation should not be null", counts != null)
    Assert.assertTrue("MembersToGroupParticipation should not be empty", counts.nonEmpty)
    Assert.assertTrue("MembersToGroupParticipation should have 1 row", counts.size == 1)
  }

  @Test
  def testCountSelectedStatusGroupedByDay(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g2"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 1
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    val dayBefore = roundHourNow.minus(1, ChronoUnit.DAYS).toLocalDate
    val dayAfter = roundHourNow.plus(1, ChronoUnit.DAYS).toLocalDate

    // act
    val counts: List[SelectedStatusGroupedByDayResults] = membersToGroupParticipationRepository.countSelectedStatusGroupedByDay(dayBefore, dayAfter, filterTimezone, version).asScala.toList

    // assert
    Assert.assertTrue("MembersToGroupParticipation should not be null", counts != null)
    Assert.assertTrue("MembersToGroupParticipation should not be empty", counts.nonEmpty)
    Assert.assertTrue("MembersToGroupParticipation should have 1 row", counts.size == 1)
  }

  @Test
  def testCountParticipationStatusGroupedByDay(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g2"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 1
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    val dayBefore = roundHourNow.minus(1, ChronoUnit.DAYS).toLocalDate
    val dayAfter = roundHourNow.plus(1, ChronoUnit.DAYS).toLocalDate

    // act
    val counts: List[ParticipationStatusGroupedByDayResults] = membersToGroupParticipationRepository.countParticipationStatusGroupedByDay(dayBefore, dayAfter, filterTimezone, version).asScala.toList

    // assert
    Assert.assertTrue("MembersToGroupParticipation should not be null", counts != null)
    Assert.assertTrue("MembersToGroupParticipation should not be empty", counts.nonEmpty)
    Assert.assertTrue("MembersToGroupParticipation should have 1 row", counts.size == 1)
  }


  @Test
  def testCountAverageMinMaxGroupSizes(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g2"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 1
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    val dayBefore = roundHourNow.minus(1, ChronoUnit.DAYS).toLocalDate
    val dayAfter = roundHourNow.plus(1, ChronoUnit.DAYS).toLocalDate

    // act
    val counts: List[AvgMinMaxGroupSize] = membersToGroupParticipationRepository.countAverageMinMaxGroupSizes(dayBefore, dayAfter, filterTimezone, version).asScala.toList

    // assert
    Assert.assertTrue("AvgMinMaxGroupSize should not be null", counts != null)
    Assert.assertTrue("AvgMinMaxGroupSize should not be empty", counts.nonEmpty)
    Assert.assertTrue("AvgMinMaxGroupSize should have 1 row", counts.size == 1)
    Assert.assertTrue("AvgMinMaxGroupSize should have min == 1", counts.head.getMingroupsize == 1)
    Assert.assertTrue("AvgMinMaxGroupSize should have max == 1", counts.head.getMaxgroupsize == 1)
    Assert.assertTrue("AvgMinMaxGroupSize should have avg == 1", counts.head.getAvggroupsize == 1)
  }


  @Test
  def testCountAverageGroupCountsInInterval(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g2"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 1
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    val dayBefore = roundHourNow.minus(1, ChronoUnit.DAYS).toLocalDate
    val dayAfter = roundHourNow.plus(1, ChronoUnit.DAYS).toLocalDate

    // act
    val counts: List[AvgGroupCountsInInterval] = membersToGroupParticipationRepository.countAverageGroupCountsInInterval(dayBefore, dayAfter, filterTimezone, version).asScala.toList

    // assert
    Assert.assertTrue("AvgMinMaxGroupSize should not be null", counts != null)
    Assert.assertTrue("AvgMinMaxGroupSize should not be empty", counts.nonEmpty)
    Assert.assertTrue("AvgMinMaxGroupSize should have 1 row", counts.size == 1)
    Assert.assertTrue("AvgMinMaxGroupSize should have avg group count in interval == 1", counts.head.getAvggroupcounts == 1)
  }

  @Test
  def testComputeAverageParticipationPerGroupPerDay(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val membersToGroupParticipation: MembersToGroupParticipation = new MembersToGroupParticipation()
    membersToGroupParticipation.composedId = new MembersToGroupParticipationIdentity("1", version)
    membersToGroupParticipation.groupName = "g2"
    membersToGroupParticipation.day = roundHourNow
    membersToGroupParticipation.hourTimeSlot = roundHourNow
    membersToGroupParticipation.memberEmail = "a@a.a"
    membersToGroupParticipation.selectedStatus = 1
    membersToGroupParticipation.participationStatus = 2
    membersToGroupParticipation.timezoneNr = "00"
    membersToGroupParticipation.timezoneStr = "UTC"
    membersToGroupParticipationRepository.save(membersToGroupParticipation)

    val dayBefore = roundHourNow.minus(1, ChronoUnit.DAYS).toLocalDate
    val dayAfter = roundHourNow.plus(1, ChronoUnit.DAYS).toLocalDate

    // act
    val counts: List[AvgParticipationPerGroupPerDayInInterval] = membersToGroupParticipationRepository.computeAverageParticipationPerGroupPerDay(dayBefore, dayAfter, filterTimezone, version).asScala.toList

    // assert
    Assert.assertTrue("AvgMinMaxGroupSize should not be null", counts != null)
    Assert.assertTrue("AvgMinMaxGroupSize should not be empty", counts.nonEmpty)
    Assert.assertTrue("AvgMinMaxGroupSize should have 1 row", counts.size == 1)
    Assert.assertTrue("AvgMinMaxGroupSize should have avg participation per group per day in interval == 1", counts.head.getAvgparticipationingroup == 1)
  }



}
