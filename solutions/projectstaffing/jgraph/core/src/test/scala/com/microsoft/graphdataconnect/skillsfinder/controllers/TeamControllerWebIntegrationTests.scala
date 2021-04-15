package com.microsoft.graphdataconnect.skillsfinder.controllers

import java.time.{LocalDateTime, ZoneOffset}

import com.microsoft.graphdataconnect.skillsfinder.db.entities.Configuration
import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.TeamMember
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.ConfigurationRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.team.TeamMemberRepository
import com.microsoft.graphdataconnect.skillsfinder.models.configs
import com.microsoft.graphdataconnect.skillsfinder.models.configs.EmployeeProfileVersionConfiguration
import com.microsoft.graphdataconnect.skillsfinder.service.ConfigurationService
import com.microsoft.graphdataconnect.skillsfinder.setup.Constants
import com.microsoft.graphdataconnect.skillsfinder.setup.web.AbstractWebIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import scala.collection.immutable.HashMap.HashTrieMap

class TeamControllerWebIntegrationTests extends AbstractWebIntegrationTestBase {

  private val log: Logger = LoggerFactory.getLogger(classOf[TeamControllerWebIntegrationTests])

  @Autowired
  var gdcRestClient: GDCRestClient = _
  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _
  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _
  @Autowired
  var teamMemberRepository: TeamMemberRepository = _
  @Autowired
  var configurationRepository: ConfigurationRepository = _
  @Autowired
  var configurationService: ConfigurationService = _

  private val employeeId = "2"
  private val v0 = TimeUtils.oldestSqlDate
  private var epV0: EmployeeProfile = new EmployeeProfile()

  private var hrepV0 = new HRDataEmployeeProfile()

  override def beforeClassSetup(): Unit = {
    // User 0
    epV0.composedId = EmployeeIdentity(employeeId, v0)
    epV0.mail = "admin2@fake.com"
    epV0.displayName = "Admin 2"
    epV0 = employeeProfileRepository.save(epV0)

    hrepV0.composedId = new HRDataEmployeeIdentity("admin2@fake.com", v0)
    hrepV0.name = "Admin 2"
    hrepV0 = hrDataEmployeeProfileRepository.save(hrepV0)

    // Team
    //    Team Member 0 V0
    //team owner
    val teamMember0: TeamMember = new TeamMember()
    teamMember0.ownerEmail = Constants.CLIENT_EMAIL

    //team member
    teamMember0.memberEmail = epV0.mail
    teamMember0.memberId = epV0.composedId.id
    teamMember0.memberName = epV0.displayName
    teamMemberRepository.save(teamMember0)

    // Set default version
    configurationRepository.save(Configuration(EmployeeProfileVersionConfiguration(v0)))
    configurationService.getLatestEmployeeProfileVersionFromCache(true)
  }

  /*
   * Add one employee into the team
   * Increment version but not not for the employee too
   * Team should contain the same member even if it is old
   */
  @Test
  @Transactional
  def oldVersionEmployeeOnTeamIsListed(): Unit = {
    val differentVersion = LocalDateTime.now(ZoneOffset.UTC)
    // Pre assert

    val teamMembers: Seq[HashTrieMap[String, String]] = gdcRestClient.getTeamMembers()
    Assert.assertTrue(teamMembers.length == 1)
    Assert.assertTrue(teamMembers.head("email") == epV0.mail)

    // Update the latest version
    configurationRepository.save(Configuration(configs.EmployeeProfileVersionConfiguration(differentVersion)))
    // refresh cache
    val newVersion: LocalDateTime = configurationService.getLatestEmployeeProfileVersionFromCache(true)
    Assert.assertTrue(newVersion == differentVersion)

    // Assert
    val teamMembersAfterUpdate = gdcRestClient.getTeamMembers()
    Assert.assertTrue(teamMembersAfterUpdate.length == 1)
    Assert.assertTrue(teamMembersAfterUpdate.head("email") == epV0.mail)
  }

  override def afterClassSetup(): Unit = {
    teamMemberRepository.deleteAll()
    employeeProfileRepository.delete(epV0)
    hrDataEmployeeProfileRepository.delete(hrepV0)

    // Set default version
    configurationRepository.save(Configuration(configs.EmployeeProfileVersionConfiguration(v0)))
    configurationService.getLatestEmployeeProfileVersionFromCache(true)
  }

}
