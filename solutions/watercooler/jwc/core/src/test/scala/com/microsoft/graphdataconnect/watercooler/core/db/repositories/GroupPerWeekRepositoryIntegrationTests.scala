/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.db.repositories

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.util

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerWeek
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.GroupPerWeekIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.GroupPerWeekRepository
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.core.setup.repository.AbstractIntegrationTestBase
import org.junit.{Assert, Test}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._

class GroupPerWeekRepositoryIntegrationTests extends AbstractIntegrationTestBase {
  private val logger: Logger = LoggerFactory.getLogger(classOf[GroupPerWeekRepositoryIntegrationTests])

  @Autowired
  var groupPerWeekRepository: GroupPerWeekRepository = _

  // TODO - add data and test
  @Test
  def testFindByDayGreaterThanEqualAndDayLessThanEqualAndComposedIdVersion(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    val filterTimezone = new util.ArrayList[String]()
    filterTimezone.add("00")

    val roundHourNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0))
    val roundHourYesterday = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0 , 0)).minus(1, ChronoUnit.DAYS)
    val groupPerWeek1: GroupPerWeek = new GroupPerWeek()
    groupPerWeek1.composedId = new GroupPerWeekIdentity("1", version)
    groupPerWeek1.day = roundHourNow
    groupPerWeek1.groupMembers = ""
    groupPerWeek1.timezoneNr = "00"
    groupPerWeek1.timezoneStr = "UTC"
    groupPerWeekRepository.save(groupPerWeek1)

    val groupPerWeek2: GroupPerWeek = new GroupPerWeek()
    groupPerWeek2.composedId = new GroupPerWeekIdentity("2", version)
    groupPerWeek2.day = roundHourYesterday
    groupPerWeek2.groupMembers = ""
    groupPerWeek2.timezoneNr = "00"
    groupPerWeek2.timezoneStr = "UTC"
    groupPerWeekRepository.save(groupPerWeek2)

    // act
    val groupPerWeekList: List[GroupPerWeek] = groupPerWeekRepository.findByDayGreaterThanEqualAndDayLessThanEqualAndComposedIdVersionAndTimezoneNrIn(roundHourYesterday, roundHourNow, version, filterTimezone).asScala.toList

    // assert
    Assert.assertTrue("groupPerWeek should not be null", groupPerWeekList != null)
    Assert.assertTrue("groupPerWeek should not be empty", groupPerWeekList.nonEmpty)
    Assert.assertTrue("groupPerWeek should have 2 rows", groupPerWeekList.size == 2)
  }

}
