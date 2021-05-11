/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.EmployeeProfile
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.HRDataEmployeeProfile
import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.{TeamInfo, TeamMember, TeamMemberSkill}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.team.{TeamInfoRepository, TeamMemberRepository, TeamMemberSkillRepository}
import com.microsoft.graphdataconnect.skillsfinder.models.business.team._
import com.microsoft.graphdataconnect.skillsfinder.service.EmployeeSearchService.computeM365EmployeeLocation
import com.microsoft.graphdataconnect.skillsfinder.utils.StringUtils.StringImplicits
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.{DataIntegrityViolationException, EmptyResultDataAccessException}
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._


@Service
class TeamService {
  private val logger: Logger = LoggerFactory.getLogger(classOf[TeamService])

  @Autowired
  var teamMemberRepository: TeamMemberRepository = _

  @Autowired
  var teamMemberSkillRepository: TeamMemberSkillRepository = _

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _

  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _

  @Autowired
  var teamInfoRepository: TeamInfoRepository = _

  @Autowired
  var configurationService: ConfigurationService = _

  def getTeamMembers(userId: String): List[TeamMemberResponse] = {
    teamMemberRepository.findByOwnerEmail(userId).toList
      .flatMap(teamMember => buildTeamMemberResponse(teamMember))
  }

  def getTeamMember(userId: String, teamMemberId: Long): TeamMemberResponse = {
    Option(teamMemberRepository.findById(teamMemberId).orElse(null))
      .flatMap(teamMember => buildTeamMemberResponse(teamMember))
      .getOrElse(throw new IllegalArgumentException("Invalid team member id"))
  }

  def saveTeamMember(teamMemberRequest: TeamMemberRequest, userId: String): Unit = {
    logger.info(s"Adding team member with id ${teamMemberRequest.email} to team of user $userId")
    val teamMember = TeamMember(teamMemberRequest, userId)
    try {
      val teamMemberSaved: TeamMember = teamMemberRepository.save(teamMember)
      teamMemberRequest.skills.foreach(skill => teamMemberSkillRepository.save(TeamMemberSkill(skill, teamMemberSaved.id)))
    } catch {
      case _: DataIntegrityViolationException =>
        logger.warn(s"Ignored an attempt to add an existing member with email ${teamMemberRequest.email} to the team of user $userId")
    }
  }

  // TODO redesign the related DB table to have the userId and the teamMemberId as part of the primary key
  def deleteTeamMember(userId: String, teamMemberId: Long): Unit = {
    logger.info(s"Removing team member with id $teamMemberId from team of user $userId")
    try {
      teamMemberRepository.deleteById(teamMemberId)
    } catch {
      case _: EmptyResultDataAccessException =>
        logger.warn(s"Ignored an attempt to remove from the team of user $userId a person ($teamMemberId) that is not part of the team")
    }
  }

  def saveTeamInfo(teamInfoRequest: TeamInfoRequestResponse, userEmail: String): Unit = {
    logger.info(s"Updating team info for user $userEmail")
    val teamInfo = TeamInfo(teamInfoRequest, userEmail)
    teamInfoRepository.save(teamInfo)
  }

  def getTeamInfo(userEmail: String): TeamInfoRequestResponse = {
    Option(teamInfoRepository.findByUserEmail(userEmail).orElse(null))
      .map((teamInfo: TeamInfo) => TeamInfoRequestResponse(teamInfo))
      .getOrElse(TeamInfoRequestResponse("", ""))
  }

  def downloadTeam(userEmail: String): TeamDownloadResponse = {
    val teamInfo: TeamInfoRequestResponse = Option(teamInfoRepository.findByUserEmail(userEmail).orElse(null))
      .map((teamInfo: TeamInfo) => TeamInfoRequestResponse(teamInfo))
      .getOrElse(throw new IllegalArgumentException("Invalid team member id"))

    val teamMembers: Seq[TeamDownloadMemberResponse] = teamMemberRepository.findByOwnerEmail(userEmail).toSeq
      .flatMap(buildTeamMemberDownloadResponse)

    TeamDownloadResponse(userEmail, teamInfo.teamName, teamInfo.teamDescription, teamMembers)
  }

  private def buildTeamMemberResponse(teamMember: TeamMember): Option[TeamMemberResponse] = {
    val employeeProfileOptional = employeeProfileRepository.findFirstByComposedIdIdOrderByComposedIdVersionDesc(teamMember.memberId)
    val employeeProfileOpt: Option[EmployeeProfile] = Option(employeeProfileOptional.orElse(null))

    employeeProfileOpt.map { employeeProfile =>
      teamMember.skills = teamMemberSkillRepository.findByTeamMemberId(teamMember.id)

      val hrDataEmployeeProfileOpt = findHRDataEmployeeProfile(teamMember.memberEmail)
      val availableSince = hrDataEmployeeProfileOpt.map(_.availableStartingFrom).orNull

      val employeeRole: String = if (!employeeProfile.jobTitle.isNullOrBlank) {
        employeeProfile.jobTitle
      } else {
        hrDataEmployeeProfileOpt.map(_.role).orNull
      }
      TeamMemberResponse.fromTeamMember(teamMember, availableSince, employeeRole, employeeProfile.profilePicture)
    }
  }

  private def buildTeamMemberDownloadResponse(teamMember: TeamMember): Option[TeamDownloadMemberResponse] = {
    val employeeProfileOptional = employeeProfileRepository.findFirstByComposedIdIdOrderByComposedIdVersionDesc(teamMember.memberId)
    val employeeProfileOpt = Option(employeeProfileOptional.orElse(null))

    employeeProfileOpt.map { employeeProfile =>
      val hrDataEmployeeProfileOpt = findHRDataEmployeeProfile(teamMember.memberEmail)

      val availableSince = hrDataEmployeeProfileOpt.map(_.availableStartingFrom).orNull
      val currentEngagement = hrDataEmployeeProfileOpt.map(_.currentEngagement).orNull

      val reportsTo: String = if (!employeeProfile.reportsTo.isNullOrBlank) {
        employeeProfile.reportsTo
      } else {
        hrDataEmployeeProfileOpt.map(_.managerName).orNull
      }

      val employeeRole: String = if (!employeeProfile.jobTitle.isNullOrBlank) {
        employeeProfile.jobTitle
      } else {
        hrDataEmployeeProfileOpt.map(_.role).orNull
      }

      // TODO move this to a common class; use the new method instead
      val employeeProfileLocation: String = computeM365EmployeeLocation(city = employeeProfile.city, state = employeeProfile.state, country = employeeProfile.country)
      // TODO move this to a common class; use the new method instead
      val employeeLocation: String = if (!employeeProfileLocation.isNullOrBlank) {
        employeeProfileLocation
      } else {
        hrDataEmployeeProfileOpt.map(_.location).orNull
      }

      TeamDownloadMemberResponse(
        name = employeeProfile.displayName,
        mail = employeeProfile.mail,
        about = employeeProfile.aboutMe,
        declaredSkills = employeeProfile.skills.toSeq.map(_.skill),
        reportsTo = reportsTo,
        availableSince = availableSince,
        currentEngagement = currentEngagement,
        role = employeeRole,
        location = employeeLocation
      )
    }

  }

  private def findHRDataEmployeeProfile(email: String): Option[HRDataEmployeeProfile] = {
    if (!email.isNullOrBlank) {
      Option(hrDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(email, configurationService.getLatestHRDataEmployeeProfileVersionFromCache()).orElse(null))
    } else {
      None
    }
  }

}
