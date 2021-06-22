/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers

import java.time.LocalDate

import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics._
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics.{AvgGroupCountsInIntervalResponse, AvgMinMaxGroupSizeResponse, AvgParticipationPerGroupPerDayInIntervalResponse, ParticipationStatusCountsResponse, ParticipationStatusGroupedByDayResultsResponse, SelectedParticipationStatusPerDayResponse, SelectedParticipationStatusResponse, SelectedStatusCountsResponse, SelectedStatusGroupedByDayResultsResponse, WeekGroupLoadResponse}
import com.microsoft.graphdataconnect.watercooler.core.managers.StatisticsManager
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/jwc/statistics"))
class StatisticsController(@Autowired statisticsManager: StatisticsManager) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[StatisticsController])


  @GetMapping(Array("/global"))
  def getGeneralStatistics(@RequestParam(name = "timezone", required = false) timezone: String,
                           @RequestParam(name = "filterTimezone", required = false) filterTimezone: String
                          ): ResponseEntity[SelectedParticipationStatusResponse] = {

    logger.info(s"retrieving the general statistics ")

    val selectedStatusResponses: Seq[SelectedStatusCountsResponse] = statisticsManager.countBySelectedStatus(timezone, filterTimezone)
    logger.info(s"retrieved ${selectedStatusResponses.length} invitation status responses")

    val participationStatusResponses: Seq[ParticipationStatusCountsResponse] = statisticsManager.countByParticipationStatus(timezone, filterTimezone)
    logger.info(s"retrieved ${participationStatusResponses.length} participation status responses")

    val response = SelectedParticipationStatusResponse(selectedStatusResponses, participationStatusResponses)
    ResponseEntity.status(HttpStatus.OK).body(response)
  }

  @GetMapping(Array("/interval"))
  def getEventsPerWeek(@RequestParam(name = "startDate", required = true)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       startDate: LocalDate,
                       @RequestParam(name = "endDate", required = true)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       endDate: LocalDate,
                       @RequestParam(name = "timezone", required = false) timezone: String,
                       @RequestParam(name = "filterTimezone", required = false) filterTimezone: String
                      ): ResponseEntity[SelectedParticipationStatusPerDayResponse] = {

    logger.info(s"Asked for statistics grouped by day in interval starting on: $startDate until $endDate with timezone: $timezone")

    val selectedStatusResponsGroupedByDay: Seq[SelectedStatusGroupedByDayResultsResponse] = statisticsManager.countSelectedStatusGroupedByDay(startDate, endDate, timezone, filterTimezone)
    logger.info(s"retrieved invitation status grouped by day: ${selectedStatusResponsGroupedByDay.length} ")

    val participationStatusResponseGroupedByDay: Seq[ParticipationStatusGroupedByDayResultsResponse] = statisticsManager.countParticipationStatusGroupedByDay(startDate, endDate, timezone, filterTimezone)
    logger.info(s"retrieved participation status grouped by day: ${participationStatusResponseGroupedByDay.length} ")


    val averageGroupCountsPerDayInIntervalResponse: List[AvgGroupCountsInIntervalResponse] = statisticsManager.countAverageGroupCountsOnInterval(startDate, endDate, timezone, filterTimezone)
    logger.info(s"retrieved average group counts per interval per day: ${averageGroupCountsPerDayInIntervalResponse.length} ")


    val avgMinMaxGroupSizeResponse: List[AvgMinMaxGroupSizeResponse] = statisticsManager.countAverageMinMaxGroupSizesOnInterval(startDate, endDate, timezone, filterTimezone)
    logger.info(s"retrieved avg,min,max group sizes per interval per day: ${avgMinMaxGroupSizeResponse.length} ")


    val averageParticipationPerGroupPerDay: List[AvgParticipationPerGroupPerDayInIntervalResponse] = statisticsManager.computeAverageParticipationPerGroupPerDay(startDate, endDate, timezone, filterTimezone)
    logger.info(s"retrieved participation per group per day: ${averageParticipationPerGroupPerDay.length} ")


    val groupCountsPerDayPerHour: WeekGroupLoadResponse = statisticsManager.computeNumberOfGroupsPerDayPerHour(startDate, endDate, timezone, filterTimezone)
    logger.info(s"retrieved WeekGroupLoadResponse: ${groupCountsPerDayPerHour} ")

    val finalResponse = statistics.SelectedParticipationStatusPerDayResponse(selectedStatusResponsGroupedByDay,
      participationStatusResponseGroupedByDay,
      averageGroupCountsPerDayInIntervalResponse,
      avgMinMaxGroupSizeResponse,
      averageParticipationPerGroupPerDay,
      groupCountsPerDayPerHour)
    ResponseEntity.status(HttpStatus.OK).body(finalResponse)
  }

}
