/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.services.group

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime}

import com.microsoft.graphdataconnect.watercooler.common.be.GroupPerWeekBE
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerWeek
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.GroupPerWeekRepository
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
class GroupPerWeekService(@Autowired configurationService: ConfigurationService,
                          @Autowired groupPerWeekRepository: GroupPerWeekRepository) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[GroupPerWeekService])

  def getGroupsBetween(startDate: LocalDate, endDate: LocalDate, filterTimezone: List[String]): List[GroupPerWeek] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    groupPerWeekRepository.findByDayGreaterThanEqualAndDayLessThanEqualAndComposedIdVersionAndTimezoneNrIn(startDate.atStartOfDay(), endDate.atStartOfDay(), latestDataVersion, filterTimezone.asJava).asScala.toList
  }

  def updateGroupPerWeekAtTimezone(groupPerDay: GroupPerWeekBE, requiredTimezone: String): GroupPerWeekBE = {
    try {
      if (null != requiredTimezone) {
        val requiredTimeZoneMinutes = (requiredTimezone.toDouble * 60).toInt
        val newLocalDateTime: LocalDateTime = groupPerDay.hourTimeSlot.plus(requiredTimeZoneMinutes, ChronoUnit.MINUTES)

        groupPerDay.copy(hour = newLocalDateTime.getHour, hourTimeSlot = newLocalDateTime)
      } else {
        groupPerDay
      }
    } catch {
      case e: Throwable =>
        logger.info(s"Cannot change date timezone of group: ${groupPerDay.groupName}", e)
        groupPerDay
    }
  }

  def updateLocalDateTime(date: LocalDateTime, requiredTimezone: String): LocalDateTime = {
    if (null != requiredTimezone) {
      val requiredTimeZoneMinutes = (requiredTimezone.toDouble * 60).toInt
      date.plus(requiredTimeZoneMinutes, ChronoUnit.MINUTES)
    } else {
      date
    }

  }

}
