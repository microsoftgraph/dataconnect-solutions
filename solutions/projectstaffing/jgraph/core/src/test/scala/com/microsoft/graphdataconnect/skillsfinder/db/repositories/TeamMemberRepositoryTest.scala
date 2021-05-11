/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.{TeamMember, TeamMemberSkill}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.team.TeamMemberRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._

class TeamMemberRepositoryTest extends AbstractIntegrationTestBase {

  @Autowired
  var teamMemberRepository: TeamMemberRepository = _

  @Test
  def test(): Unit = {

    val jackSkill1 = new TeamMemberSkill
    jackSkill1.skill = "concurrent programming"

    val jackSkill2 = new TeamMemberSkill
    jackSkill2.skill = "functional programming"

    val teamMemberJack = new TeamMember
    teamMemberJack.memberEmail = "jack@company.com"
    teamMemberJack.memberName = "Jack"
    teamMemberJack.ownerEmail = "3"
    teamMemberJack.memberId = "m365_id1"
    teamMemberJack.skills = List(jackSkill1, jackSkill2).asJava

    teamMemberRepository.save(teamMemberJack)

    val teamMembersFound = teamMemberRepository.findByOwnerEmail(teamMemberJack.ownerEmail).asScala;
    Assert.assertTrue(teamMembersFound.length == 1)
    val teamMemberFound = teamMembersFound.head

    Assert.assertTrue("Got wrong team member", teamMemberFound.memberEmail == teamMemberJack.memberEmail)
    Assert.assertTrue("Wrong employeeId", teamMemberFound.memberId == teamMemberJack.memberId)
    Assert.assertTrue("Wrong name", teamMemberFound.memberName == teamMemberJack.memberName)
    Assert.assertTrue("Wrong opportunity id", teamMemberFound.ownerEmail == teamMemberJack.ownerEmail)
    Assert.assertArrayEquals("The skills should match", teamMemberJack.skills.toArray, teamMemberFound.skills.toArray)

  }

}
