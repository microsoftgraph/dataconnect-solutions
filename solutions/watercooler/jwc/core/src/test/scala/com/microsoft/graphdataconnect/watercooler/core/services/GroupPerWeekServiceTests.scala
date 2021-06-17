/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.services

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.microsoft.graphdataconnect.watercooler.common.be.GroupPerWeekBE
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.GroupPerWeekRepository
import com.microsoft.graphdataconnect.watercooler.common.services.group.GroupPerWeekService
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.week.GroupPerWeekResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.{BeforeClass, Test}
import org.mockito.Mockito.mock

class GroupPerWeekServiceTests {

  @Test
  def testUpdateGroupPerDayAtTimezone(): Unit = {
    // Arrange
    val nowDate = LocalDateTime.now()
    val groupPerDay: GroupPerWeekBE = GroupPerWeekBE("g1", "2021-01-01 13:01:02", "G1", nowDate.getHour, nowDate, Array())
    // Act
    val updatedGroup = GroupPerWeekServiceTests.service.updateGroupPerWeekAtTimezone(groupPerDay, "1")

    // Assert
    val newDate = nowDate.plus(1 * 60, ChronoUnit.MINUTES)
    assertThat(updatedGroup.tzinfo).isEqualTo("2021-01-01 13:01:02")  // same
    assertThat(updatedGroup.hour).isEqualTo(newDate.getHour) // add 1 hour
    assertThat(updatedGroup.hourTimeSlot).isEqualTo(newDate)
  }

}

object GroupPerWeekServiceTests {
  var groupPerWeekRepository: GroupPerWeekRepository = mock(classOf[GroupPerWeekRepository])
  var configurationService: ConfigurationService = mock(classOf[ConfigurationService])
  var service: GroupPerWeekService = _

  @BeforeClass
  def setup() {
    service = new GroupPerWeekService(configurationService, groupPerWeekRepository)
  }
}
