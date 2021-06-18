/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator.management

import java.time.temporal.ChronoUnit
import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graph.models.{Attendee, AttendeeType, BodyType, DateTimeTimeZone, EmailAddress, Event, ItemBody, Location}
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerDayCC
import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLogger
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import com.microsoft.graphdataconnect.watercooler.common.services.graph.GraphService
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.events.creator.config.SendMailConfig

// https://docs.microsoft.com/en-us/graph/api/calendar-post-events?view=graph-rest-1.0&tabs=java
class CalendarEventCreator(val groupMailBodyWriter: GroupMailBodyWriter,
                           val teamsMeetingCreator: TeamsMeetingCreator,
                           val sendMailConfig: SendMailConfig)
                          (implicit val log: JwcLogger,
                           val objectMapper: ObjectMapper) extends Serializable {


  def sendInvite(graphApiAppAccessInfo: GraphApiAppAccessInfo, group: GroupPerDayCC, meetingOrganizerMail: String): Boolean = {
    log.info(s"database date: ${group.hour_time_slot} for group: ${group.display_name}")

    val graphService: GraphService =  GraphService(graphApiAppAccessInfo)

    val meetingOrganizerId: String = graphService.getUserId(meetingOrganizerMail)

    log.info(s"Creating Teams meeting for group: ${group.group_name}")
    val teamsMeetingUrl: String = teamsMeetingCreator.createTeamsMeeting(graphService, group, meetingOrganizerId, sendMailConfig.meetingDurationInMinutes)

    log.info(s"Sending mail invitation for group: ${group.group_name}")

    val event = new Event
    event.subject = group.display_name + " Watercooler Event"
    val body = new ItemBody
    body.contentType = BodyType.HTML
    body.content = groupMailBodyWriter.getMeetingBody(group, teamsMeetingUrl)
    event.body = body
    val start = new DateTimeTimeZone
    start.dateTime = TimeUtils.localDateTimeToAzureString(TimeUtils.databaseTimestampToLocalDateTime(group.hour_time_slot))
//    start.dateTime = "2021-05-04T18:00:00"
    start.timeZone = "Etc/GMT"
    event.start = start
    val end = new DateTimeTimeZone
    end.dateTime = TimeUtils.localDateTimeToAzureString(TimeUtils.databaseTimestampToLocalDateTime(group.hour_time_slot).plus(sendMailConfig.meetingDurationInMinutes, ChronoUnit.MINUTES))
//    end.dateTime = "2021-05-04T18:30:00"
    end.timeZone = "Etc/GMT"
    event.end = end
    val location = new Location
    location.displayName = "Microsoft Teams"
    event.location = location
    event.attendees = createAttendeeList(group)

    graphService.createEvent(meetingOrganizerId, event)
    true
  }

  private def createAttendeeList(group: GroupPerDayCC): util.LinkedList[Attendee] = {
    val attendeesList = new util.LinkedList[Attendee]()
    val members: Map[String, Map[String, String]] = objectMapper.readValue(group.group_members, classOf[Map[String, Map[String, String]]])

    members.foreach { member =>

        val attendees = new Attendee
        val emailAddress = new EmailAddress
        emailAddress.address = member._1
        emailAddress.name = member._2("name")
        attendees.emailAddress = emailAddress
        attendees.`type` = AttendeeType.REQUIRED
        attendeesList.add(attendees)
    }

    attendeesList
  }



}


