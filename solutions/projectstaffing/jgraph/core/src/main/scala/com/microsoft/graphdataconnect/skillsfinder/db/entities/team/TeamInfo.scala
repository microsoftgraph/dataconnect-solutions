/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.entities.team

import com.fasterxml.jackson.annotation.JsonProperty
import com.microsoft.graphdataconnect.skillsfinder.models.business.team.TeamInfoRequestResponse
import javax.persistence.{Column, Entity, Id, Table}
import javax.validation.constraints.{Email, NotBlank}

object TeamInfo {

  def apply(teamInfoRequest: TeamInfoRequestResponse, userEmail: String): TeamInfo = {
    val teamInfo: TeamInfo = new TeamInfo
    teamInfo.userEmail = userEmail
    teamInfo.description = teamInfoRequest.teamDescription
    teamInfo.name = teamInfoRequest.teamName
    teamInfo
  }

}

@Table(name = "team_info")
@Entity
class TeamInfo {

  @Id
  @JsonProperty("userEmail")
  @Column(name = "user_email")
  @NotBlank
  @Email
  var userEmail: String = _

  @JsonProperty("name")
  @Column(name = "name")
  @NotBlank
  var name: String = _

  @JsonProperty("description")
  @Column(name = "description", columnDefinition = "text")
  @NotBlank
  var description: String = _

}
