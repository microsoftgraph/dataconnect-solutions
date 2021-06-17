/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.services.group

import java.time.LocalDate
import java.util

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics._
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.MembersToGroupParticipation
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.{AvgGroupCountsInInterval, AvgMinMaxGroupSize, AvgParticipationPerGroupPerDayInInterval, ParticipationStatusCounts, ParticipationStatusGroupedByDayResults, SelectedStatusCounts, SelectedStatusGroupedByDayResults}
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.MembersToGroupParticipationRepository
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
class MembersToGroupParticipationService(@Autowired configurationService: ConfigurationService,
                                         @Autowired membersToGroupParticipationRepository: MembersToGroupParticipationRepository) {


  private val logger: Logger = LoggerFactory.getLogger(classOf[MembersToGroupParticipationService])


  def findMemberInGroup(memberEmail: String, groupName: String): Option[MembersToGroupParticipation] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    Option(membersToGroupParticipationRepository.findFirstByGroupNameAndMemberEmailAndComposedIdVersion(groupName, memberEmail, latestDataVersion).orElse(null))
  }

  def findGroupsStatus(groupNames: List[String]): List[MembersToGroupParticipation] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.findByGroupNameInAndComposedIdVersion(groupNames.asJava, latestDataVersion).asScala.toList
  }

  /*
   * used for statistics
   */

  def countBySelectedStatus(filterTimezone: List[String]): util.List[SelectedStatusCounts] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.countBySelectedStatus(latestDataVersion, filterTimezone.asJava)
  }

  def countByParticipationStatus(filterTimezone: List[String]): util.List[ParticipationStatusCounts] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.countByParticipationStatus(latestDataVersion, filterTimezone.asJava)
  }

  def countSelectedStatusGroupedByDay(startDate: String, endDate: String, filterTimezone: List[String]): util.List[SelectedStatusGroupedByDayResults] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.countSelectedStatusGroupedByDay(LocalDate.parse(startDate), LocalDate.parse(endDate), filterTimezone.asJava, latestDataVersion)
  }

  def countParticipationStatusGroupedByDay(startDate: String, endDate: String, filterTimezone: List[String]): util.List[ParticipationStatusGroupedByDayResults] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.countParticipationStatusGroupedByDay(LocalDate.parse(startDate), LocalDate.parse(endDate), filterTimezone.asJava, latestDataVersion)
  }

  def countSelectedStatusGroupedByDay(startDate: LocalDate, endDate: LocalDate, filterTimezone: List[String]): util.List[SelectedStatusGroupedByDayResults] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.countSelectedStatusGroupedByDay(startDate, endDate,  filterTimezone.asJava, latestDataVersion)
  }

  def countParticipationStatusGroupedByDay(startDate: LocalDate, endDate: LocalDate, filterTimezone: List[String]): util.List[ParticipationStatusGroupedByDayResults] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.countParticipationStatusGroupedByDay(startDate, endDate, filterTimezone.asJava, latestDataVersion)
  }


  def countAverageMinMaxGroupSizesInInterval(startDate: LocalDate, endDate: LocalDate, filterTimezone: List[String]): util.List[AvgMinMaxGroupSize] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.countAverageMinMaxGroupSizes(startDate, endDate, filterTimezone.asJava, latestDataVersion)
  }

  def countAvgGroupCountsInInterval(startDate: LocalDate, endDate: LocalDate, filterTimezone: List[String]): util.List[AvgGroupCountsInInterval] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.countAverageGroupCountsInInterval(startDate, endDate, filterTimezone.asJava, latestDataVersion)
  }


  def computeAverageParticipationPerGroupPerDay(startDate: LocalDate, endDate: LocalDate, filterTimezone: List[String]): util.List[AvgParticipationPerGroupPerDayInInterval] = {
    val latestDataVersion = configurationService.getLatestGroupsDataVersionFromCache()
    membersToGroupParticipationRepository.computeAverageParticipationPerGroupPerDay(startDate, endDate, filterTimezone.asJava, latestDataVersion)
  }





}
