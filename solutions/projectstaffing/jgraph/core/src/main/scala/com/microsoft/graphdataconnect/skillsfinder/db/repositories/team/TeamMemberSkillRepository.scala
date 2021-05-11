/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.team

import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.TeamMemberSkill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait TeamMemberSkillRepository extends JpaRepository[TeamMemberSkill, Long] {

  /**
   * Retrieves an entity by its email.
   *
   * @param teamMemberId must not be { @literal null or empty}.
   * @return the entity with the given id or { @literal List#empty()} if none found
   * @throws IllegalArgumentException if { @code email} is { @literal null or empty string, or TeamMemberSkill not found}.
   */
  def findByTeamMemberId(@Param("team_member_id") teamMemberId: Long): java.util.List[TeamMemberSkill]

}
