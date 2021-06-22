/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.repositories.group

import java.time.{LocalDate, LocalDateTime}
import java.util

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics._
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.MembersToGroupParticipation
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.MembersToGroupParticipationIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.{AvgGroupCountsInInterval, AvgMinMaxGroupSize, AvgParticipationPerGroupPerDayInInterval, ParticipationStatusCounts, ParticipationStatusGroupedByDayResults, SelectedStatusCounts, SelectedStatusGroupedByDayResults}
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait MembersToGroupParticipationRepository extends JpaRepository[MembersToGroupParticipation, MembersToGroupParticipationIdentity] {



  def findFirstByGroupNameAndMemberEmailAndComposedIdVersion(@Param("groupName") groupName: String,
                                                             @Param("memberEmail") memberEmail: String,
                                                             @Param("version") version: LocalDateTime): java.util.Optional[MembersToGroupParticipation]

  def findByGroupNameInAndComposedIdVersion(@Param("groupNameList") groupNameList: java.util.List[String],
                                            @Param("version") version: LocalDateTime): java.util.List[MembersToGroupParticipation]


  //====================== statistics parameters =================================

  /**
    * Computes how many people with a given participation_status status code are
    *
    * @return
    */
  @Query(
    value = "select participation_status as participationstatus,count(*) as counts from members_to_group_participation mtgp " +
      " where mtgp.version = :version and timezone_nr in (:timezoneFilter)" +
      " group by participation_status",
    nativeQuery = true)
  def countByParticipationStatus(@Param("version") version: LocalDateTime,
                                 @Param("timezoneFilter") timezoneFilter: java.util.List[String]): java.util.List[ParticipationStatusCounts]

  /**
    * Computes how many people with a given selected status code are
    *
    * @return
    */
  @Query(
    value = "select invitation_status as selectedstatus,count(*) as counts from members_to_group_participation mtgp  " +
      " where mtgp.version = :version and timezone_nr in (:timezoneFilter)" +
      " group by invitation_status",
    nativeQuery = true)
  def countBySelectedStatus(@Param("version") version: LocalDateTime,
                            @Param("timezoneFilter") timezoneFilter: java.util.List[String]): java.util.List[SelectedStatusCounts]

  /**
    * Computes the SelectedStatus grouped by day
    *
    * @param startDate
    * @param endDate
    * @return
    */
  @Query(
    value = "select day,invitation_status as selectedstatus,count(*) as counts from members_to_group_participation mtgp " +
      " where mtgp.version = :version and mtgp .[day] >= :startDate and mtgp.[day]<= :endDate and timezone_nr in (:timezoneFilter) " +
      " group by invitation_status,day order by day asc",
    nativeQuery = true)
  def countSelectedStatusGroupedByDay(@Param("startDate") startDate: LocalDate,
                                      @Param("endDate") endDate: LocalDate,
                                      @Param("timezoneFilter") timezoneFilter: java.util.List[String],
                                      @Param("version") version: LocalDateTime): java.util.List[SelectedStatusGroupedByDayResults]

  /**
    * Computes the participation status statistics group by day
    *
    * @param startDate
    * @param endDate
    * @return
    */
  @Query(
    value = "select day,participation_status as participationstatus,count(*) as counts from members_to_group_participation mtgp " +
      " where mtgp.version = :version and mtgp.[day] >= :startDate and mtgp.[day]<= :endDate and timezone_nr in (:timezoneFilter) " +
      " group by participation_status,day order by day asc",
    nativeQuery = true)
  def countParticipationStatusGroupedByDay(@Param("startDate") startDate: LocalDate,
                                           @Param("endDate") endDate: LocalDate,
                                           @Param("timezoneFilter") timezoneFilter: java.util.List[String],
                                           @Param("version") version: LocalDateTime): java.util.List[ParticipationStatusGroupedByDayResults]


  @Query(
    value="select avg(a.group_size) as avggroupsize,max(a.group_size) as maxgroupsize,min(a.group_size) as mingroupsize " +
      "from " +
      "(select count(distinct mtgp.member_email) as group_size from dbo.members_to_group_participation mtgp " +
      "where mtgp.version = :version and mtgp.[day] >= :startDate and mtgp.[day] <= :endDate and timezone_nr in (:timezoneFilter) " +
      "group by mtgp.group_name) a",
    nativeQuery = true
  )
  def countAverageMinMaxGroupSizes(@Param("startDate") startDate: LocalDate,
                                   @Param("endDate") endDate: LocalDate,
                                   @Param("timezoneFilter") timezoneFilter: java.util.List[String],
                                   @Param("version") version: LocalDateTime): java.util.List[AvgMinMaxGroupSize]


  @Query(
    value="select avg(a.group_counts) as avggroupcounts from " +
      "(select mtgp.day as day,count(distinct mtgp.group_name) as group_counts " +
      "from dbo.members_to_group_participation mtgp where " +
      "mtgp.version = :version and mtgp.[day] >= :startDate and mtgp.[day] <= :endDate and timezone_nr in (:timezoneFilter) " +
      "group by mtgp.[day] ) as a",
    nativeQuery = true
  )
  def countAverageGroupCountsInInterval(startDate: LocalDate, endDate: LocalDate, timezoneFilter: java.util.List[String], version: LocalDateTime): util.List[AvgGroupCountsInInterval]

  @Query(
    value="select avg(a.count_particitation) as avgparticipationingroup from " +
      "(select mtgp.[day],mtgp.group_name,count(mtgp.member_email) as count_particitation " +
      "from dbo.members_to_group_participation mtgp  where mtgp.participation_status = 2 and " +
      "mtgp.version = :version and mtgp.[day] >= :startDate and mtgp.[day] <= :endDate and timezone_nr in (:timezoneFilter) " +
      "group by mtgp.group_name,mtgp.[day]) as a",
    nativeQuery = true
  )
  def computeAverageParticipationPerGroupPerDay(startDate: LocalDate, endDate: LocalDate, timezoneFilter: java.util.List[String], version: LocalDateTime):util.List[AvgParticipationPerGroupPerDayInInterval]



}
