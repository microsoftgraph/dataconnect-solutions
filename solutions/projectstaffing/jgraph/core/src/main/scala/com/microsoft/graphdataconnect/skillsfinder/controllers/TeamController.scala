/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.skillsfinder.models.business.team.{TeamDownloadResponse, TeamInfoRequestResponse, TeamMemberRequest, TeamMemberResponse}
import com.microsoft.graphdataconnect.skillsfinder.models.response.ResponseMessage
import com.microsoft.graphdataconnect.skillsfinder.service.{TeamService, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

@Controller
@RequestMapping(Array("/gdc"))
class TeamController(@Autowired teamService: TeamService, @Autowired userService: UserService) {

  @PostMapping(Array("/team/structure/members"))
  def saveTeamMember(@RequestBody(required = true) teamMemberRequest: TeamMemberRequest, @RequestAttribute("userId") userId: String): ResponseEntity[ResponseMessage] = {
    teamMemberRequest.validate()
    teamService.saveTeamMember(teamMemberRequest, userId)
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Team member saved"))
  }

  @GetMapping(Array("/team/structure/members"))
  def getTeamMembers(@RequestAttribute("userId") userId: String): ResponseEntity[List[TeamMemberResponse]] = {
    ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamMembers(userId))
  }

  @GetMapping(Array("/team/structure/members/{teamMemberId}"))
  def getTeamMember(@PathVariable teamMemberId: Long, @RequestAttribute("userId") userId: String): ResponseEntity[TeamMemberResponse] = {
    ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamMember(userId, teamMemberId))
  }

  @DeleteMapping(Array("/team/structure/members/{teamMemberId}"))
  def deleteTeamMember(@PathVariable teamMemberId: Long, @RequestAttribute("userId") userId: String): ResponseEntity[String] = {
    teamService.deleteTeamMember(userId, teamMemberId)
    ResponseEntity.status(HttpStatus.OK).body("Team member deleted successfully")
  }

  @PutMapping(Array("/team/info"))
  def saveTeamInfo(@RequestBody(required = true) teamInfoRequest: TeamInfoRequestResponse, @RequestAttribute("userId") userId: String): ResponseEntity[ResponseMessage] = {
    teamService.saveTeamInfo(teamInfoRequest, userId)
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Team information saved"))
  }

  @GetMapping(Array("/team/info"))
  def getTeamInfo(@RequestAttribute("userId") userId: String): ResponseEntity[TeamInfoRequestResponse] = {
    ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamInfo(userId))
  }

  @GetMapping(Array("/team/download"))
  def downloadTeam(@RequestAttribute("userId") userId: String): ResponseEntity[TeamDownloadResponse] = {
    ResponseEntity.status(HttpStatus.OK).body(teamService.downloadTeam(userId))
  }
}
