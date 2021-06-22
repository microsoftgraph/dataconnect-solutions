/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.services.group

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.util

import com.microsoft.graphdataconnect.watercooler.common.be.GroupPerDayBE
import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.watercooler.common.be.{GroupPerDayBE, GroupPerDayMemberBE}
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.NrOfGroupsPerDayPerHourInInterval
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.GroupPerDayRepository
import com.microsoft.graphdataconnect.watercooler.common.dto.DistinctTimezoneDTO
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
class GroupPerDayService(@Autowired configurationService: ConfigurationService,
                         @Autowired groupPerDayRepository: GroupPerDayRepository,
                         @Autowired objectMapper: ObjectMapper) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[GroupPerDayService])

  def getGroupsPerDay(day: LocalDate, requiredTimeZone: Int, filterTimezone: List[String]): List[GroupPerDayBE] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()

    val groups = groupPerDayRepository.getGroupsOnDay(day, requiredTimeZone, filterTimezone.asJava, latestDataVersion).asScala.toList

    groups.map(group => {
      val membersPicture: Map[String, Map[String, String]] = objectMapper.readValue(group.groupMembers, classOf[Map[String, Map[String, String]]])
      val members: Map[String, GroupPerDayMemberBE] = membersPicture.map(x => {
        (x._1 -> GroupPerDayMemberBE(x._2("name"), x._2("image")))
      })
      // TODO deserialization fails when GroupPerDayMemberBE object is used
      //      val members: Map[String, GroupPerDayMemberBE] = objectMapper.readValue(group.groupMembers, classOf[Map[String, GroupPerDayMemberBE]])
      GroupPerDayBE(
        id = group.composedId.id,
        day = group.day,
        hourTimeSlot = group.hourTimeSlot,
        hour = group.hour,
        groupName = group.groupName,
        displayName = group.displayName,
        groupMembers = members
      )
    })

  }

  def getGroupsPerHourOfDay(day: LocalDate, hour: Int, requiredTimeZone: Int): List[GroupPerDayBE] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    val requiredTime = LocalDateTime.of(day, LocalTime.of(hour, 0, 0)).minus(requiredTimeZone, ChronoUnit.MINUTES)
    val groups = groupPerDayRepository.getGroupsOnHourOfDay(requiredTime, latestDataVersion).asScala.toList

    groups.map(group => {
      // TODO deserialization fails when GroupPerDayMemberBE object is used
      //      val membersPicture: Map[String, Map[String, String]] = objectMapper.readValue(group.groupMembers, classOf[Map[String, Map[String, String]]])
      //      val members: Map[String, GroupPerDayMemberBE] = objectMapper.readValue(group.groupMembers, classOf[Map[String, GroupPerDayMemberBE]])

      val membersPicture: Map[String, Map[String, String]] = objectMapper.readValue(group.groupMembers, classOf[Map[String, Map[String, String]]])
      val members: Map[String, GroupPerDayMemberBE] = membersPicture.map(x => {
        (x._1 -> GroupPerDayMemberBE(x._2("name"), x._2("image")))
      })

      GroupPerDayBE(
        id = group.composedId.id,
        day = group.day,
        hourTimeSlot = group.hourTimeSlot,
        hour = group.hour,
        groupName = group.groupName,
        displayName = group.displayName,
        groupMembers = members
      )
    })

  }

  def updateGroupPerDayAtTimezone(groupPerDayDTO: GroupPerDayBE, requiredTimezone: String): GroupPerDayBE = {
    try {
      if (null != requiredTimezone) {
        val requiredTimeZoneMinutes = (requiredTimezone.toDouble * 60).toInt
        val newLocalDateTime: LocalDateTime =  groupPerDayDTO.hourTimeSlot.plus(requiredTimeZoneMinutes, ChronoUnit.MINUTES)
        groupPerDayDTO.copy(hour = newLocalDateTime.getHour, day = newLocalDateTime)
      } else groupPerDayDTO
    } catch {
      case e: Throwable =>
        logger.error(s"Cannot change date timezone of group: ${groupPerDayDTO.id}", e)
        groupPerDayDTO
    }

  }

  def updateHourAtTimezone(hour: Int, requiredTimezone: String): Int = {
    try {
      if (null != requiredTimezone) {
        val requiredTimeZoneMinutes = (requiredTimezone.toDouble * 60).toInt
        val timeNow = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, 0, 0))
        val newLocalDateTime: LocalDateTime = timeNow.plus(requiredTimeZoneMinutes, ChronoUnit.MINUTES)

        newLocalDateTime.getHour
      } else hour
    } catch {
      case e: Exception => hour
    }
  }

  def computeNumberOfGroupsPerDayPerHour(startDate: LocalDate, endDate: LocalDate, requiredTimezoneHoursOffset: Int, filterTimezone: List[String]): util.List[NrOfGroupsPerDayPerHourInInterval] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    groupPerDayRepository.computeNumberOfGroupsPerDayPerHour(startDate, endDate, requiredTimezoneHoursOffset, filterTimezone.asJava, latestDataVersion)
  }

  def getDistinctTimezoneFilters(): List[DistinctTimezoneDTO] = {
    val dbTimezones = groupPerDayRepository.getDistinctTimezone()
    dbTimezones.asScala.map { timezone =>
      DistinctTimezoneDTO(timezone.getName(), timezone.getValue())
    }.toList
  }

}
