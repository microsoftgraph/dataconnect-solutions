/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.team

import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.TeamInfo

object TeamInfoRequestResponse {

  def apply(teamInfo: TeamInfo): TeamInfoRequestResponse = {
    TeamInfoRequestResponse(teamName = teamInfo.name, teamDescription = teamInfo.description)
  }

}

case class TeamInfoRequestResponse(teamName: String, teamDescription: String)
