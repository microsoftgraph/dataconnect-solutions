/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.repositories.group

import java.time.{LocalDate, LocalDateTime}
import java.util

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerDay
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.GroupPerDayIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.models.meta.DistinctTimezone
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.NrOfGroupsPerDayPerHourInInterval
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait GroupPerDayRepository extends JpaRepository[GroupPerDay, GroupPerDayIdentity]{

  @Query(
    value = "select * from groups_per_day " +
      "where  version = :version" +
      " and hour_time_slot = :hourTimeSlot",
    nativeQuery = true)
  def getGroupsOnHourOfDay(@Param("hourTimeSlot") hourTimeSlot: LocalDateTime,
                           @Param("version") latestDataVersion: LocalDateTime): java.util.List[GroupPerDay]

  @Query(
    value = "select * from groups_per_day " +
      " where version = :version" +
      " and :day = CONVERT(DATE, dateadd(minute, :timezone, hour_time_slot), 101)" +
      " and timezone_nr in (:filterTimezone)",
    nativeQuery = true)
  def getGroupsOnDay(@Param("day") hourTimeSlot: LocalDate,
                     @Param("timezone") timezone: Int,
                     @Param("filterTimezone") filterTimezone: java.util.List[String],
                     @Param("version") latestDataVersion: LocalDateTime): java.util.List[GroupPerDay]


  @Query(
    value="select dayofweek,abs(24 + hour + :requiredTimezoneHoursOffset) % 24 as hour,count(hour) as nrofgroups from " +
      "(SELECT FORMAT(gpd.[day], 'ddd') AS dayofweek,hour from  dbo.groups_per_day gpd where " +
      "gpd.version = :version and gpd.[day] >= :startDate and gpd.[day] <= :endDate and timezone_nr in (:filterTimezone)) a " +
      "group by dayofweek,hour order by dayofweek,hour",
    nativeQuery = true
  )
  def computeNumberOfGroupsPerDayPerHour(startDate: LocalDate, endDate: LocalDate, requiredTimezoneHoursOffset: Int,  filterTimezone: java.util.List[String], version: LocalDateTime):util.List[NrOfGroupsPerDayPerHourInInterval]

  @Query(
    value = "select gpd.timezone_str as name, gpd.timezone_nr as value from groups_per_day gpd group by gpd.timezone_str, gpd.timezone_nr",
    nativeQuery = true
  )
  def getDistinctTimezone(): util.List[DistinctTimezone]
}
