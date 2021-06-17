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

//      if(member._1.contains("darius")) {
        val attendees = new MeetingParticipantInfo
        val identitySet: IdentitySet = new IdentitySet
        val identity: Identity = new Identity
        identity.id = graphService.getUserId(member._1)
        identity.displayName = member._2("name")
        identitySet.user = identity
        attendees.identity = identitySet
        attendeesList.add(attendees)
//      }
    }

    attendeesList.addAll(createFakeMeetingAttendeeList())
    attendeesList
  }

  private def createFakeMeetingAttendeeList(): util.LinkedList[MeetingParticipantInfo] = {
    val attendeesList = new util.LinkedList[MeetingParticipantInfo]()
    List(
      Tuple2[String, String]("f0e2ad9e-8786-4c78-8a81-e947c58abed6", "andrei@bpcsdevtest.onmicrosoft.com"),
      Tuple2[String, String]("c0559d7a-c917-4fa8-8457-d7d2baf40451", "alexandru@bpcsdevtest.onmicrosoft.com"),
      Tuple2[String, String]("0c5abb9f-f045-4864-9ad3-56b77e9ed088", "claudiu@bpcsit.com"),
      Tuple2[String, String]("05c90ffb-e749-4eee-9e35-4144950c3524", "gary@bpcsit.com"),

      Tuple2[String, String]("56b8c797-745d-42ec-9ac0-6f33fe6f6762", "Claudiu@bpcs.com")
    ).foreach { case member: Tuple2[String, String] =>

        val attendees = new MeetingParticipantInfo
        val identitySet: IdentitySet = new IdentitySet
        val identity: Identity = new Identity
        identity.id = member._1
        identity.displayName = member._2
        identitySet.user = identity
        attendees.identity = identitySet
        attendeesList.add(attendees)
    }

    attendeesList
  }

}
