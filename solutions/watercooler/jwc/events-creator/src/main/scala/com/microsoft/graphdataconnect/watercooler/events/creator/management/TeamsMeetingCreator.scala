/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator.management

import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graph.models.{Identity, IdentitySet, MeetingParticipantInfo, MeetingParticipants, OnlineMeeting}
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerDayCC
import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLogger
import com.microsoft.graphdataconnect.watercooler.common.services.graph.GraphService
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils

//https://docs.microsoft.com/en-us/graph/api/application-post-onlinemeetings?view=graph-rest-1.0&tabs=http
class TeamsMeetingCreator()
                         (implicit val log: JwcLogger,
                          val objectMapper: ObjectMapper) extends Serializable {


  def createTeamsMeeting(graphService: GraphService, group: GroupPerDayCC, meetingOrganizerId: String, meetingDurationInMinutes: Int): String = {
    val onlineMeeting: OnlineMeeting = new OnlineMeeting

    val startTimeWithOffset = TimeUtils.databaseTimestampToLocalDateTime(group.hour_time_slot).atOffset(ZoneOffset.UTC)
    val endTimeWithOffset = TimeUtils.databaseTimestampToLocalDateTime(group.hour_time_slot).plus(meetingDurationInMinutes, ChronoUnit.MINUTES).atOffset(ZoneOffset.UTC)

//    onlineMeeting.startDateTime = OffsetDateTimeSerializer.deserialize("2021-05-11T18:00:00.0000000+00:00")
//    onlineMeeting.endDateTime = OffsetDateTimeSerializer.deserialize("2021-05-11T18:30:00.0000000+00:00")

    onlineMeeting.startDateTime = startTimeWithOffset
    onlineMeeting.endDateTime = endTimeWithOffset
    onlineMeeting.subject = s"${group.display_name} Watercooler Event"

    val meetingParticipants: MeetingParticipants = new MeetingParticipants
    meetingParticipants.attendees = createMeetingAttendeeList(graphService, group)
    onlineMeeting.participants = meetingParticipants

    val graphMeetingRawResponse: OnlineMeeting = graphService.createTeamsEvent(meetingOrganizerId, onlineMeeting)

    graphMeetingRawResponse.joinWebUrl
  }

  private def createMeetingAttendeeList(graphService: GraphService, group: GroupPerDayCC): util.LinkedList[MeetingParticipantInfo] = {
    val attendeesList = new util.LinkedList[MeetingParticipantInfo]()
    val members: Map[String, Map[String, String]] = objectMapper.readValue(group.group_members, classOf[Map[String, Map[String, String]]])
    members.foreach { member =>

        val attendees = new MeetingParticipantInfo
        val identitySet: IdentitySet = new IdentitySet
        val identity: Identity = new Identity
        identity.id = graphService.getUserId(member._1)
        identity.displayName = member._2("name")
        identitySet.user = identity
        attendees.identity = identitySet
        attendeesList.add(attendees)
    }

    attendeesList
  }

}
