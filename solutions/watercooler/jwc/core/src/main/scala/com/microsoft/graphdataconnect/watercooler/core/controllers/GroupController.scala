/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers

import java.time.LocalDate

import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.UpdateGroupParticipationResponse
import com.microsoft.graphdataconnect.watercooler.core.models.UserToken
import com.microsoft.graphdataconnect.watercooler.core.services.UserTokenService
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.{MembersGroupPersonalMeetingResponse, UpdateGroupParticipationResponse}
import com.microsoft.graphdataconnect.watercooler.core.exceptions.PermissionConsentMissingException
import com.microsoft.graphdataconnect.watercooler.core.managers.GroupsManager
import com.microsoft.graphdataconnect.watercooler.core.models.{TokenScope, UserToken}
import com.microsoft.graphdataconnect.watercooler.core.services.{ADFService, UserTokenService}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/jwc/group"))
class GroupController(@Autowired groupsManager: GroupsManager,
                      @Autowired userTokenService: UserTokenService,
                      @Autowired adfService: ADFService) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[GroupController])

  @GetMapping(Array("/day/{day}"))
  def getEventsPerDay(@PathVariable(name = "day", required = true)
                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                      day: LocalDate,
                      @RequestParam(name = "hour", required = false) hour: Integer,
                      @RequestParam(name = "timezone", required = false) timezone: String,
                      @RequestParam(name = "filterTimezone", required = false) filterTimezone: String
                     ): ResponseEntity[List[_]] = {

    logger.info(s"Asked for events on day: $day, hour: $hour and timezone: $timezone")
    val result = if (null == hour) {
      groupsManager.getGroupsPerDay(day, timezone, filterTimezone)
    } else {
      groupsManager.getGroupsPerHourOfDay(day, hour, timezone)
    }
    ResponseEntity.status(HttpStatus.OK).body(result)
  }

  @GetMapping(Array("/week"))
  def getEventsPerWeek(@RequestParam(name = "startDate", required = true)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       startDate: LocalDate,
                       @RequestParam(name = "endDate", required = true)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       endDate: LocalDate,
                       @RequestParam(name = "timezone", required = false) timezone: String,
                       @RequestParam(name = "filterTimezone", required = false) filterTimezone: String
                      ): ResponseEntity[List[_]] = {

    logger.info(s"Asked for events on week starting on: $startDate until $endDate with timezone: $timezone")
    val result = groupsManager.getGroupsPerWeek(startDate, endDate, timezone, filterTimezone)
    ResponseEntity.status(HttpStatus.OK).body(result)
  }

  @GetMapping(Array("/slots"))
  def getBusySlotsPerGroup(@RequestParam(name = "name", required = true) groupName: String,
                           @RequestParam(name = "timezone", required = false) timezone: String,
                           @RequestParam(name = "filterTimezone", required = false) filterTimezone: String
                          ): ResponseEntity[MembersGroupPersonalMeetingResponse] = {

    logger.info(s"Asked for group: ${groupName} busy slots for required timezone: $timezone")
    val result: MembersGroupPersonalMeetingResponse = groupsManager.getBusySlots(groupName, timezone, filterTimezone)
    ResponseEntity.status(HttpStatus.OK).body(result)
  }

  @PostMapping(Array("/update-participation"))
  def updateGroupParticipationStatus(@RequestHeader httpHeaders: HttpHeaders,
                                     @CookieValue(value = "AppServiceAuthSession") authSessionCookie: String): ResponseEntity[UpdateGroupParticipationResponse] = {
    try {
      implicit val userToken: UserToken = userTokenService.getUserToken(httpHeaders, authSessionCookie, TokenScope.AZURE_MANAGEMENT_SCOPE)
      // TODO use database state in order to create a lock that works event if the application is running in a distributed environment
      this.synchronized {
        if (!adfService.isUpdateUserEventsAttendancePipelineRunning()) {
          adfService.startUpdateUserEventsAttendancePipeline()
          logger.info("Started update of user group attendance")
          ResponseEntity.ok().body(UpdateGroupParticipationResponse(message = "In a few minutes the engagement information for all groups will be updated. "))
        } else {
          logger.info("The process of updating the user group attendance is already running!")
          ResponseEntity.status(HttpStatus.CONFLICT).body(UpdateGroupParticipationResponse(message = "The update process is already running!"))
        }
      }
    } catch {
      case permissionException: PermissionConsentMissingException =>
        logger.error("Failed to update attendance status for groups.", permissionException)
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UpdateGroupParticipationResponse(permissionConsentLink = permissionException.consentRequestUrl))
    }
  }


}
