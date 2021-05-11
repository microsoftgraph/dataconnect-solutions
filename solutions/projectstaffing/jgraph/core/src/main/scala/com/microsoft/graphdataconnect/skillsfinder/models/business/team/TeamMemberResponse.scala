/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.team

import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.TeamMember

import scala.collection.JavaConversions._

object TeamMemberResponse {

  def fromTeamMember(teamMember: TeamMember, availableSince: String, employeeRole: String, employeeProfilePicture: String): TeamMemberResponse = {
    val teamMemberResponse = TeamMemberResponse(
      teamMemberId = teamMember.id,
      userId = teamMember.ownerEmail,
      employeeId = teamMember.memberId,
      email = teamMember.memberEmail,
      name = teamMember.memberName,
      skills = teamMember.skills.toList.map(skill => skill.skill),
      availableSince = availableSince,
      employeeRole = employeeRole,
      employeeProfilePicture = employeeProfilePicture)

    teamMemberResponse
  }

}

case class TeamMemberResponse(teamMemberId: Long,
                              userId: String,
                              employeeId: String,
                              email: String,
                              name: String,
                              skills: List[String],
                              availableSince: String,
                              employeeRole: String,
                              employeeProfilePicture: String)

