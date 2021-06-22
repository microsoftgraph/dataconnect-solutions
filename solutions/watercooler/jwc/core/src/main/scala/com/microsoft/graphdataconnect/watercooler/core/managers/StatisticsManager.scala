/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.managers

import java.time.LocalDate
import java.util

import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics._
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics._
import com.microsoft.graphdataconnect.watercooler.common.services.group.MembersToGroupParticipationService
import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.{AvgGroupCountsInInterval, AvgMinMaxGroupSize, AvgParticipationPerGroupPerDayInInterval, NrOfGroupsPerDayPerHourInInterval, ParticipationStatusCounts, ParticipationStatusGroupedByDayResults, SelectedStatusCounts, SelectedStatusGroupedByDayResults}
import com.microsoft.graphdataconnect.watercooler.common.services.group.{GroupPerDayService, MembersToGroupParticipationService}
import com.microsoft.graphdataconnect.watercooler.common.util.StringUtils
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.GroupPerDayResponse
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics.{AvgGroupCountsInIntervalResponse, AvgMinMaxGroupSizeResponse, AvgParticipationPerGroupPerDayInIntervalResponse, NrOfGroupsPerDayPerHourInIntervalResponse, ParticipationStatusCountsResponse, ParticipationStatusGroupedByDayResultsResponse, SelectedStatusCountsResponse, SelectedStatusGroupedByDayResultsResponse, WeekGroupLoadResponse}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._


@Component
class StatisticsManager(@Autowired membersToGroupParticipationService: MembersToGroupParticipationService,
                        @Autowired groupPerDayService: GroupPerDayService,
                        @Autowired objectMapper: ObjectMapper) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[StatisticsManager])


  /**
    * Returns the counts by invitation to selection to meeting
    *
    * @param requiredTimezone
    * @return
    */
  def countBySelectedStatus(requiredTimezone: String, filterTimezoneStr: String): Seq[SelectedStatusCountsResponse] = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val response: util.List[SelectedStatusCounts] = membersToGroupParticipationService.countBySelectedStatus(filterTimezone)
    val finalResponse: Seq[SelectedStatusCountsResponse] = response.asScala.toList.map {
      case invitationCounts: SelectedStatusCounts =>
        SelectedStatusCountsResponse.from(invitationCounts)
    }
    finalResponse
  }

  /**
    * Returns the counts by participation to meeting
    *
    * @param requiredTimezone
    * @return
    */
  def countByParticipationStatus(requiredTimezone: String, filterTimezoneStr: String): List[ParticipationStatusCountsResponse] = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val response: util.List[ParticipationStatusCounts] = membersToGroupParticipationService.countByParticipationStatus(filterTimezone)
    val finalResponse: List[ParticipationStatusCountsResponse] = response.asScala.toList.map {
      case invitationCounts: ParticipationStatusCounts =>
        ParticipationStatusCountsResponse.from(invitationCounts)
    }
    finalResponse
  }

  def countSelectedStatusGroupedByDay(startDate: String, endDate: String, requiredTimezone: String, filterTimezoneStr: String): List[GroupPerDayResponse] = {
    List.empty
  }

  def countParticipationStatusGroupedByDay(startDate: String, endDate: String, requiredTimezone: String, filterTimezoneStr: String): List[GroupPerDayResponse] = {
    List.empty
  }

  def countParticipationStatusGroupedByDay(startDate: LocalDate, endDate: LocalDate, requiredTimezone: String, filterTimezoneStr: String): List[ParticipationStatusGroupedByDayResultsResponse] = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val response: util.List[ParticipationStatusGroupedByDayResults] = membersToGroupParticipationService.countParticipationStatusGroupedByDay(startDate, endDate, filterTimezone)
    val finalResponse: List[ParticipationStatusGroupedByDayResultsResponse] = response.asScala.toList.map {
      case record: ParticipationStatusGroupedByDayResults =>
        ParticipationStatusGroupedByDayResultsResponse.from(record)
    }
    finalResponse
  }

  def countSelectedStatusGroupedByDay(startDate: LocalDate, endDate: LocalDate, requiredTimezone: String, filterTimezoneStr: String): List[SelectedStatusGroupedByDayResultsResponse] = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val response: util.List[SelectedStatusGroupedByDayResults] = membersToGroupParticipationService.countSelectedStatusGroupedByDay(startDate, endDate, filterTimezone)
    val finalResponse: List[SelectedStatusGroupedByDayResultsResponse] = response.asScala.toList.map {
      case record: SelectedStatusGroupedByDayResults =>
        SelectedStatusGroupedByDayResultsResponse.from(record)
    }
    finalResponse
  }


  def countAverageMinMaxGroupSizesOnInterval(startDate: LocalDate, endDate: LocalDate, requiredTimezone: String, filterTimezoneStr: String): List[AvgMinMaxGroupSizeResponse] = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val response: util.List[AvgMinMaxGroupSize] = membersToGroupParticipationService.countAverageMinMaxGroupSizesInInterval(startDate, endDate, filterTimezone)
    val finalResponse: List[AvgMinMaxGroupSizeResponse] = response.asScala.toList.filter(p=>p!=null).map {
      case record: AvgMinMaxGroupSize =>
        AvgMinMaxGroupSizeResponse.from(record)
    }
    finalResponse
  }


  def countAverageGroupCountsOnInterval(startDate: LocalDate, endDate: LocalDate, requiredTimezone: String, filterTimezoneStr: String): List[AvgGroupCountsInIntervalResponse] = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val response: util.List[AvgGroupCountsInInterval] = membersToGroupParticipationService.countAvgGroupCountsInInterval(startDate, endDate, filterTimezone)
    if(response == null) {
      return List.empty
    }
    val finalResponse: List[AvgGroupCountsInIntervalResponse] = response.asScala.toList.filter(p=>p!=null).map {
      case record: AvgGroupCountsInInterval =>
        AvgGroupCountsInIntervalResponse.from(record)
    }
    finalResponse
  }


  def computeAverageParticipationPerGroupPerDay(startDate: LocalDate, endDate: LocalDate, requiredTimezone: String, filterTimezoneStr: String): List[AvgParticipationPerGroupPerDayInIntervalResponse] = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val response: util.List[AvgParticipationPerGroupPerDayInInterval] = membersToGroupParticipationService.computeAverageParticipationPerGroupPerDay(startDate, endDate, filterTimezone)
    val finalResponse: List[AvgParticipationPerGroupPerDayInIntervalResponse] = response.asScala.toList.filter(p=>p!=null).map {
      case record: AvgParticipationPerGroupPerDayInInterval =>
        AvgParticipationPerGroupPerDayInIntervalResponse.from(record)
    }
    finalResponse
  }

  def computeNumberOfGroupsPerDayPerHour(startDate: LocalDate, endDate: LocalDate, requiredTimezone: String, filterTimezoneStr: String): WeekGroupLoadResponse = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val requiredTimezoneHoursOffset: Int = convertRequiredTimezoneToHours(requiredTimezone)
    val response: util.List[NrOfGroupsPerDayPerHourInInterval] = groupPerDayService.computeNumberOfGroupsPerDayPerHour(startDate, endDate, requiredTimezoneHoursOffset, filterTimezone)
    val finalResponse: List[NrOfGroupsPerDayPerHourInIntervalResponse] = response.asScala.toList.filter(p=>p!=null).map {
      case record: NrOfGroupsPerDayPerHourInInterval =>
        NrOfGroupsPerDayPerHourInIntervalResponse.from(record)
    }

    WeekGroupLoadResponse.from(finalResponse)
  }

  private def convertRequiredTimezoneToHours(requiredTimezone: String): Int = try {
    requiredTimezone.toDouble.toInt
  } catch {
    case e:Throwable =>
      logger.error(s"Cannot parse required timezone: $requiredTimezone", e)
      0
  }


}
