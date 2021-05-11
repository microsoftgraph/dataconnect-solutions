/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.entities.team

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence._

object TeamMemberSkill {

  def apply(newSkill: String, teamMemberId: Long): TeamMemberSkill = {
    val teamMemberSkill = new TeamMemberSkill
    teamMemberSkill.skill = newSkill
    teamMemberSkill.teamMemberId = teamMemberId
    teamMemberSkill
  }

}

@Table(name = "team_member_skills")
@Entity
class TeamMemberSkill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @Column(name = "id", insertable = false, nullable = false)
  var id: Long = _

  @JsonProperty("teamMemberId")
  @JoinColumn(name = "team_member_id")
  var teamMemberId: Long = _

  @JsonProperty("skill")
  @Column(name = "skill")
  var skill: String = _

}
