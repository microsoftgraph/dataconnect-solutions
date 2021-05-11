/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.services

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.EmployeeProfile
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.HRDataEmployeeProfile
import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.{TeamMember, TeamMemberSkill}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.team.{TeamMemberRepository, TeamMemberSkillRepository}
import com.microsoft.graphdataconnect.skillsfinder.models.business.team.TeamMemberResponse
import com.microsoft.graphdataconnect.skillsfinder.service.{ConfigurationService, TeamService}
import org.junit.{Assert, BeforeClass, Test}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}

import scala.compat.java8.OptionConverters._

class TeamServiceTests {

  @Test
  def testGetTeamMembersWithoutHRData(): Unit = {
    // Arrange

    when(TeamServiceTests.mockConfigurationService.getLatestHRDataEmployeeProfileVersionFromCache()).thenReturn(LocalDateTime.now())
    when(TeamServiceTests.mockHRDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(java.util.Optional.empty[HRDataEmployeeProfile]())

    val employeeProfile = new EmployeeProfile()
    when(TeamServiceTests.mockEmployeeProfileRepository.findFirstByComposedIdIdOrderByComposedIdVersionDesc(any[String])).thenReturn(Some(employeeProfile).asJava)

    val expectedSkills: java.util.List[TeamMemberSkill] = new java.util.ArrayList[TeamMemberSkill]()
    when(TeamServiceTests.mockTeamMemberSkillRepository.findByTeamMemberId(any[Long])).thenReturn(expectedSkills)

    val teamMember = new TeamMember()
    teamMember.memberEmail = "a"
    val expectedResult: java.util.List[TeamMember] = new java.util.ArrayList[TeamMember]()
    expectedResult.add(teamMember)

    when(TeamServiceTests.mockTeamMemberRepository.findByOwnerEmail(any[String])).thenReturn(expectedResult)

    // Act
    val teamMembers: List[TeamMemberResponse] = TeamServiceTests.service.getTeamMembers("1")

    // Assert
    Assert.assertTrue("Returned Team members should have one result", teamMembers.nonEmpty)
    Assert.assertEquals("Id should match", teamMembers.head.email, teamMember.memberEmail)
  }

  @Test
  def testGetTeamMemberWithoutHRData(): Unit = {
    // Arrange

    when(TeamServiceTests.mockConfigurationService.getLatestHRDataEmployeeProfileVersionFromCache()).thenReturn(LocalDateTime.now())
    when(TeamServiceTests.mockHRDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(java.util.Optional.empty[HRDataEmployeeProfile]())

    val employeeProfile = new EmployeeProfile()
    when(TeamServiceTests.mockEmployeeProfileRepository.findFirstByComposedIdIdOrderByComposedIdVersionDesc(any[String])).thenReturn(Some(employeeProfile).asJava)

    val expectedSkills: java.util.List[TeamMemberSkill] = new java.util.ArrayList[TeamMemberSkill]()
    when(TeamServiceTests.mockTeamMemberSkillRepository.findByTeamMemberId(any[Long])).thenReturn(expectedSkills)

    val teamMember = new TeamMember()
    teamMember.memberEmail = "a"

    when(TeamServiceTests.mockTeamMemberRepository.findById(any[Long])).thenReturn(Some(teamMember).asJava)

    // Act
    val teamMembers: TeamMemberResponse = TeamServiceTests.service.getTeamMember("1", 1L)

    // Assert
    Assert.assertTrue("Returned Team member should have one result", teamMembers != null)
    Assert.assertEquals("Returned Team member should the correct email", teamMembers.email, teamMember.memberEmail)
  }

}

object TeamServiceTests {
  var mockTeamMemberRepository: TeamMemberRepository = _
  var mockHRDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _
  var mockEmployeeProfileRepository: EmployeeProfileRepository = _
  var mockTeamMemberSkillRepository: TeamMemberSkillRepository = _
  var mockConfigurationService: ConfigurationService = _

  var service: TeamService = _

  @BeforeClass
  def setup() {
    mockTeamMemberRepository = mock(classOf[TeamMemberRepository])
    mockHRDataEmployeeProfileRepository = mock(classOf[HRDataEmployeeProfileRepository])
    mockEmployeeProfileRepository = mock(classOf[EmployeeProfileRepository])
    mockTeamMemberSkillRepository = mock(classOf[TeamMemberSkillRepository])
    mockConfigurationService = mock(classOf[ConfigurationService])

    service = new TeamService()
    service.hrDataEmployeeProfileRepository = mockHRDataEmployeeProfileRepository
    service.teamMemberRepository = mockTeamMemberRepository
    service.employeeProfileRepository = mockEmployeeProfileRepository
    service.teamMemberSkillRepository = mockTeamMemberSkillRepository
    service.configurationService = mockConfigurationService
  }
}
