package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.TeamInfo
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.team.TeamInfoRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

class TeamInfoRepositoryTest extends AbstractIntegrationTestBase {

  @Autowired
  var teamInfoRepository: TeamInfoRepository = _

  @Test
  def testThatFindByEmailWorksAsExpected(): Unit = {
    val teamInfo1 = new TeamInfo
    teamInfo1.userEmail = "user1@company.com"
    teamInfo1.name = "Team1"
    teamInfo1.description = "description for Team1"

    val teamInfo2 = new TeamInfo
    teamInfo2.userEmail = "user2@company.com"
    teamInfo2.name = "Team2"
    teamInfo2.description = "description for Team2"

    teamInfoRepository.save(teamInfo1)
    teamInfoRepository.save(teamInfo2)

    Assert.assertTrue(Option(teamInfoRepository.findByUserEmail(teamInfo1.userEmail).orElse(null))
      .exists(teamInfo => teamInfo.userEmail == teamInfo1.userEmail && teamInfo.name == teamInfo1.name && teamInfo.description == teamInfo1.description))
  }

}
