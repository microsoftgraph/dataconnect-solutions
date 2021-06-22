/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.services.group

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, LocalTime}

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.MembersGroupPersonalMeeting
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.MembersGroupPersonalMeetingRepository
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MembersGroupPersonalMeetingService(@Autowired configurationService: ConfigurationService,
                                         @Autowired membersGroupPersonalMeetingRepository: MembersGroupPersonalMeetingRepository) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[MembersGroupPersonalMeetingService])

  def findByGroupName(groupName: String): Option[MembersGroupPersonalMeeting] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    Option(membersGroupPersonalMeetingRepository.findFirstByGroupNameAndComposedIdVersion(groupName, latestDataVersion).orElse(null))
  }

  def updateTimezone(busySlots: Map[String, Object], requiredTimezone: String): Map[String, Object] = {
    busySlots.map { case (k: String, v: Object) =>
      val timeNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(k.toInt, 0, 0))
      val requiredTimeZoneMinutes = (requiredTimezone.toDouble * 60).toInt
      val newLocalDateTime: LocalDateTime =  timeNow.plus(requiredTimeZoneMinutes, ChronoUnit.MINUTES)

      (newLocalDateTime.getHour.toString, v)
    }
  }

}
