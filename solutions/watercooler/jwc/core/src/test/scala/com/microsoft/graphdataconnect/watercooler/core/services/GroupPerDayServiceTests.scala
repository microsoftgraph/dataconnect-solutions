/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.services

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.watercooler.common.be.GroupPerDayBE
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerDay
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.GroupPerDayRepository
import com.microsoft.graphdataconnect.watercooler.common.services.group.GroupPerDayService
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.{BeforeClass, Test}
import org.mockito.Mockito.mock

class GroupPerDayServiceTests {

  @Test
  def testUpdateGroupPerDayAtTimezone(): Unit = {
    // Arrange
    val nowDate = LocalDateTime.now()
    val groupPerDay: GroupPerDay = new GroupPerDay()
    groupPerDay.hour = nowDate.getHour
    groupPerDay.hourTimeSlot = nowDate
    // Act
    val groupPerDayBE: GroupPerDayBE = GroupPerDayBE(
      id = null,
      day = groupPerDay.day,
      hourTimeSlot = groupPerDay.hourTimeSlot,
      hour = groupPerDay.hour,
      groupName = groupPerDay.groupName,
      displayName = groupPerDay.displayName,
      groupMembers = Map.empty
    )
    val updatedGroup: GroupPerDayBE = GroupPerDayServiceTests.service.updateGroupPerDayAtTimezone(groupPerDayBE, "2")

    // Assert
    val newDate = nowDate.plus(2 * 60, ChronoUnit.MINUTES)
    assertThat(updatedGroup.hour).isEqualTo(newDate.getHour) // add 2 hours
    assertThat(updatedGroup.day).isEqualTo(newDate)
    assertThat(updatedGroup.hourTimeSlot).isEqualTo(nowDate)
  }

}

object GroupPerDayServiceTests {
  var groupPerDayRepository: GroupPerDayRepository = mock(classOf[GroupPerDayRepository])
  var configurationService: ConfigurationService = mock(classOf[ConfigurationService])
  var service: GroupPerDayService = _

  val objectMapper: ObjectMapper = new ObjectMapper()

  @BeforeClass
  def setup() {
    service = new GroupPerDayService(configurationService, groupPerDayRepository, objectMapper)
  }
}


