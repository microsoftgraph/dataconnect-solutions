/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.NrOfGroupsPerDayPerHourInInterval


case class NrOfGroupsPerDayPerHourInIntervalResponse(dayOfWeek: String, hour: Int, nrOfGroups: Int)

object NrOfGroupsPerDayPerHourInIntervalResponse {
  def from(p: NrOfGroupsPerDayPerHourInInterval): NrOfGroupsPerDayPerHourInIntervalResponse = {
    NrOfGroupsPerDayPerHourInIntervalResponse(p.getDayofweek, p.getHour, p.getNrofgroups)
  }
}


case class WeekGroupLoadResponse(mon: Map[Int, Int],
                                 tue: Map[Int, Int],
                                 wed: Map[Int, Int],
                                 thu: Map[Int, Int],
                                 fri: Map[Int, Int])

object WeekGroupLoadResponse {
  def from(allEntries: List[NrOfGroupsPerDayPerHourInIntervalResponse]): WeekGroupLoadResponse = {
    var mon: Map[Int, Int] = Map()
    var tue: Map[Int, Int] = Map()
    var wed: Map[Int, Int] = Map()
    var thu: Map[Int, Int] = Map()
    var fri: Map[Int, Int] = Map()

    Range(0, 23).map {
      hour =>
        mon += (hour -> 0)
        wed += (hour -> 0)
        tue += (hour -> 0)
        thu += (hour -> 0)
        fri += (hour -> 0)
    }
    allEntries.foreach {
      groupsPerDayPerHour =>
        groupsPerDayPerHour.dayOfWeek.toLowerCase match {
          case "mon" =>
            mon += (Math.abs(groupsPerDayPerHour.hour) -> groupsPerDayPerHour.nrOfGroups)
          case "tue" =>
            tue += (Math.abs(groupsPerDayPerHour.hour) -> groupsPerDayPerHour.nrOfGroups)
          case "wed" =>
            wed += (Math.abs(groupsPerDayPerHour.hour)-> groupsPerDayPerHour.nrOfGroups)
          case "thu" =>
            thu += (Math.abs(groupsPerDayPerHour.hour) -> groupsPerDayPerHour.nrOfGroups)
          case "fri" =>
            fri += (Math.abs(groupsPerDayPerHour.hour) -> groupsPerDayPerHour.nrOfGroups)

        }
    }

    WeekGroupLoadResponse(mon, tue, wed, thu, fri)

  }
}