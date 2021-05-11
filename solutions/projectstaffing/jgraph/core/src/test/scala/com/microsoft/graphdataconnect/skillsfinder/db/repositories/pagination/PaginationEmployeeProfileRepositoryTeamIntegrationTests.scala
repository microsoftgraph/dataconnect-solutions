/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.pagination

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.TeamMember
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.team.TeamMemberRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

class PaginationEmployeeProfileRepositoryTeamIntegrationTests extends AbstractIntegrationTestBase {

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _
  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _

  @Autowired
  var teamMemberRepository: TeamMemberRepository = _

  val version = TimeUtils.oldestSqlDate

  val employeeProfile1: EmployeeProfile = new EmployeeProfile()
  employeeProfile1.composedId = EmployeeIdentity("123", version)
  employeeProfile1.mail = "admin1@fake.com"
  employeeProfile1.displayName = "Admin 1"

  val hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
  hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
  hrDataProfile1.availableStartingFrom = "2020-10-10" // First to be available


  val employeeProfile2: EmployeeProfile = new EmployeeProfile()
  employeeProfile2.composedId = EmployeeIdentity("2", version)
  employeeProfile2.mail = "admin2@fake.com"
  employeeProfile2.displayName = "Admin 2"

  val hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
  hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
  hrDataProfile2.availableStartingFrom = "2022-10-10" // Last to be available


  val teamMemberOwner: TeamMember = new TeamMember() // Team owner is member of own team
  teamMemberOwner.ownerEmail = employeeProfile1.mail
  teamMemberOwner.memberEmail = employeeProfile1.mail
  teamMemberOwner.memberId = employeeProfile1.composedId.id
  teamMemberOwner.memberName = employeeProfile1.displayName

  val teamMemberOther: TeamMember = new TeamMember() // Other employee is is member of team owner's team
  teamMemberOther.ownerEmail = employeeProfile1.mail
  teamMemberOther.memberEmail = employeeProfile2.mail
  teamMemberOther.memberId = employeeProfile2.composedId.id
  teamMemberOther.memberName = employeeProfile2.displayName

  /*
   * Team
   * without any recommendation
   */
  @Test
  def pagination_DefaultEmployeesList_TeamMembersOther_MarkOther(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOther)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10",
      teamMemberOwner.ownerEmail, null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("All employees should be returned", 2, result.size())

    Assert.assertTrue("Results should be ordered by availability", result.get(0).employee_id == teamMemberOwner.memberId &&
      result.get(1).employee_id == teamMemberOther.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == false &&
      result.get(1).included_in_current_team == true)
  }

  @Test
  def pagination_DefaultEmployeesList_TeamMembersAll_MarkAll(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOwner)
    teamMemberRepository.save(teamMemberOther)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10",
      teamMemberOwner.ownerEmail, null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("All employees should be returned", 2, result.size())

    Assert.assertTrue("Results should be ordered by availability", result.get(0).employee_id == teamMemberOwner.memberId &&
      result.get(1).employee_id == teamMemberOther.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == true &&
      result.get(1).included_in_current_team == true)
  }

  @Test
  def pagination_DefaultEmployeesList_TeamMembersSelf_MarkSelf(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOwner)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10",
      teamMemberOwner.ownerEmail, null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("All employees should be returned", 2, result.size())

    Assert.assertTrue("Results should be ordered by availability", result.get(0).employee_id == teamMemberOwner.memberId &&
      result.get(1).employee_id == teamMemberOther.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == true &&
      result.get(1).included_in_current_team == false)
  }

  /*
   * Team
   * with Recommended employees
   */

  @Test
  def pagination_RecommendSelf_TeamMembersOther_MarkNone(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOther)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10",
      teamMemberOwner.ownerEmail, s"'${teamMemberOwner.memberEmail}'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("Should return as many employees as recommended", 1, result.size())

    Assert.assertTrue("Recommended employee should be returned", result.get(0).employee_id == teamMemberOwner.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == false)
  }

  @Test
  def pagination_RecommendAll_TeamMembersOther_MarkOther(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOther)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10",
      teamMemberOwner.ownerEmail, s"'${teamMemberOther.memberEmail}', '${teamMemberOwner.memberEmail}'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("All employees should be returned", 2, result.size())

    Assert.assertTrue("Results should be ordered by recommended order", result.get(0).employee_id == teamMemberOther.memberId &&
      result.get(1).employee_id == teamMemberOwner.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == true &&
      result.get(1).included_in_current_team == false)
  }

  @Test
  def pagination_RecommendAll_TeamMembersAll_MarkAll(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOwner)
    teamMemberRepository.save(teamMemberOther)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10",
      teamMemberOwner.ownerEmail, s"'${teamMemberOther.memberEmail}', '${teamMemberOwner.memberEmail}'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("All employees should be returned", 2, result.size())

    Assert.assertTrue("Results should be ordered by recommended order", result.get(0).employee_id == teamMemberOther.memberId &&
      result.get(1).employee_id == teamMemberOwner.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == true &&
      result.get(1).included_in_current_team == true)
  }

  @Test
  def pagination_RecommendAll_TeamMembersSelf_MarkSelf(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOwner)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10",
      teamMemberOwner.ownerEmail, s"'${teamMemberOther.memberEmail}', '${teamMemberOwner.memberEmail}'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("All employees should be returned", 2, result.size())

    Assert.assertTrue("Results should be ordered by recommended order", result.get(0).employee_id == teamMemberOther.memberId &&
      result.get(1).employee_id == teamMemberOwner.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == false &&
      result.get(1).included_in_current_team == true)
  }

  @Test
  def pagination_RecommendSelf_TeamMembersSelf_MarkSelf(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOwner)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10",
      teamMemberOwner.ownerEmail, s"'${teamMemberOwner.memberEmail}'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("Should return as many employees as recommended", 1, result.size())

    Assert.assertTrue("Recommended employee should be returned", result.get(0).employee_id == teamMemberOwner.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == true)
  }

  @Test
  def pagination_RecommendOther_TeamMembersSelf_MarkNone(): Unit = {
    // Arrange
    hrDataEmployeeProfileRepository.save(hrDataProfile1)
    hrDataEmployeeProfileRepository.save(hrDataProfile2)
    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    teamMemberRepository.save(teamMemberOwner)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10",
      teamMemberOwner.ownerEmail, s"'${teamMemberOther.memberEmail}'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertEquals("Should return as many employees as recommended", 1, result.size())

    Assert.assertTrue("Recommended employee should be returned", result.get(0).employee_id == teamMemberOther.memberId)

    Assert.assertTrue("Team members should be marked in results", result.get(0).included_in_current_team == false)
  }

}
