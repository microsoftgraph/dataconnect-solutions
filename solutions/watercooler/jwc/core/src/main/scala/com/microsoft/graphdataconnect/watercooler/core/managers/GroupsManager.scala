/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.managers

import java.time.LocalDate

import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.GroupMemberStatusDTO
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.week.GroupPerWeekDayResponse
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.MembersGroupPersonalMeeting
import com.microsoft.graphdataconnect.watercooler.common.services.group.MembersToGroupParticipationService
import com.microsoft.graphdataconnect.watercooler.common.util.MeetingStatusUtils
import com.microsoft.graphdataconnect.watercooler.common.be.GroupPerWeekBE
import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.watercooler.common.be.{GroupPerDayBE, GroupPerWeekBE}
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.{GroupPerWeek, MembersGroupPersonalMeeting, MembersToGroupParticipation}
import com.microsoft.graphdataconnect.watercooler.common.exceptions.NotFoundException
import com.microsoft.graphdataconnect.watercooler.common.services.group.{GroupPerDayService, GroupPerWeekService, MembersGroupPersonalMeetingService, MembersToGroupParticipationService}
import com.microsoft.graphdataconnect.watercooler.common.util.{MeetingStatusUtils, StringUtils}
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.week.{GroupPerWeekDayResponse, GroupPerWeekResponse}
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.{GroupMemberStatusDTO, GroupPerDayResponse, GroupPerHourOfDayResponse, MembersGroupPersonalMeetingResponse}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class GroupsManager(@Autowired groupPerDayService: GroupPerDayService,
                    @Autowired membersToGroupParticipationService: MembersToGroupParticipationService,
                    @Autowired groupPerWeekService: GroupPerWeekService,
                    @Autowired membersGroupPersonalMeetingService: MembersGroupPersonalMeetingService,
                    @Autowired objectMapper: ObjectMapper) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[GroupsManager])


  def getGroupsPerDay(day: LocalDate, requiredTimezone: String, filterTimezoneStr: String): List[GroupPerDayResponse] = {
    val requiredTimezoneMinutesOffset: Int = convertRequiredTimezoneMinutesOffset(requiredTimezone)
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val groups: List[GroupPerDayBE] = groupPerDayService.getGroupsPerDay(day, requiredTimezoneMinutesOffset, filterTimezone)
    val groupsWithRequiredTimezone = groups.map(x=> groupPerDayService.updateGroupPerDayAtTimezone(x, requiredTimezone))

    groupsWithRequiredTimezone.groupBy(_.hour).map(x=> GroupPerDayResponse(x._1, x._2.size)).toList

  }

  def getGroupsPerHourOfDay(day: LocalDate, hour: Int, requiredTimezone: String): List[GroupPerHourOfDayResponse] = {
    val requiredTimezoneMinutesOffset: Int = convertRequiredTimezoneMinutesOffset(requiredTimezone)

    val groups: List[GroupPerDayBE] = groupPerDayService.getGroupsPerHourOfDay(day, hour, requiredTimezoneMinutesOffset)

    val groupsNames = groups.map(_.groupName)
    val groupMemberParticipationFlat: List[MembersToGroupParticipation] = membersToGroupParticipationService.findGroupsStatus(groupsNames)
    val groupMemberParticipation: Map[String, Map[String, List[MembersToGroupParticipation]]] = groupMemberParticipationFlat.groupBy(_.groupName).map(x => (x._1 -> x._2.groupBy(_.memberEmail)))

    groups.map { group => {
      val groupMembers = groupMemberParticipation(group.groupName) // TODO safe get
//      val membersPicture: Map[String, Map[String, String]] = objectMapper.readValue(group.groupMembers, classOf[Map[String, Map[String, String]]])
      val members = group.groupMembers.map { case (memberEmail, memberData) =>
        val memberStatus = Some(groupMembers(memberEmail).head) // TODO safe get
        GroupMemberStatusDTO(
          email = memberEmail,
          wcStatus = MeetingStatusUtils.statusToString(memberStatus.map(_.selectedStatus)),
          attendanceStatus = MeetingStatusUtils.statusToString(memberStatus.map(_.participationStatus)),
          image = memberData.image,
          name = memberData.name
        )
      }.toList
      val membersAccepted = members.filter(x => x.attendanceStatus == "accepted")

      val groupAtRequiredTimezone = groupPerDayService.updateGroupPerDayAtTimezone(group, requiredTimezone)

      GroupPerHourOfDayResponse(
        day = groupAtRequiredTimezone.day.toString,
        time = groupAtRequiredTimezone.hour,
        name = group.groupName,
        displayName = group.displayName,
        attendanceRate = membersAccepted.size * 100 / members.size,
        members = members
      )
    }}
  }

  def getGroupsPerWeek(startDate: LocalDate, endDate: LocalDate, requiredTimezone: String, filterTimezoneStr: String): List[GroupPerWeekDayResponse] = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)
    val dbGroupsPerWeek: List[GroupPerWeek] = groupPerWeekService.getGroupsBetween(startDate, endDate, filterTimezone)

    val everyGroupOnWeek: List[GroupPerWeekBE] = dbGroupsPerWeek.flatMap{ groupPerWeek =>
      val groups: List[GroupPerWeekBE] = objectMapper.readValue(groupPerWeek.groupMembers, classOf[Array[GroupPerWeekBE]]).toList
      groups.map(x => groupPerWeekService.updateGroupPerWeekAtTimezone(x, requiredTimezone))
    }

    val everyGroupOnWeekGroupedPerDay: Map[LocalDate, List[GroupPerWeekBE]] = everyGroupOnWeek.groupBy(_.hourTimeSlot.toLocalDate)
    val groupsPerWeekResponse: List[GroupPerWeekDayResponse] = everyGroupOnWeekGroupedPerDay.map{ group =>
      GroupPerWeekDayResponse(group._1.toString, group._2.toArray.map(x => GroupPerWeekResponse.fromBE(x)))
    }.toList

    groupsPerWeekResponse

  }

  def getBusySlots(groupName: String, requiredTimezone: String, filterTimezoneStr: String): MembersGroupPersonalMeetingResponse = {
    val filterTimezone: List[String] = StringUtils.stringUrlParamToList(filterTimezoneStr)

    val group: MembersGroupPersonalMeeting = membersGroupPersonalMeetingService.findByGroupName(groupName).getOrElse(throw new NotFoundException(s"Canot find group: $groupName"))

    val busySlotsJson: Map[String, Object] = objectMapper.readValue(group.groupBusySlots, classOf[Map[String, Object]])

    val busySlots = if (null != requiredTimezone) {
      membersGroupPersonalMeetingService.updateTimezone(busySlotsJson, requiredTimezone)
    } else busySlotsJson

    MembersGroupPersonalMeetingResponse(
      id= group.composedId.id,
      groupName = group.groupName,
      groupBusySlots = busySlots
    )
  }

  private def convertRequiredTimezoneMinutesOffset(requiredTimezone: String): Int = try {
    requiredTimezone.toDouble.toInt * 60
  } catch {
    case e:Throwable =>
      logger.error(s"Cannot parse required timezone: $requiredTimezone", e)
      0
  }

}
