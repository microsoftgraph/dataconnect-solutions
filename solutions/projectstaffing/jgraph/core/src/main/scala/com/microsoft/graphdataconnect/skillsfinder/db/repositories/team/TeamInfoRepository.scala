/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.team

import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.TeamInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait TeamInfoRepository extends JpaRepository[TeamInfo, String] {

  /**
   * Retrieves an entity by user_email.
   *
   * @param userEmail must not be { @literal null or empty}.
   * @return the entity or { @literal Optional#empty()} if none found
   * @throws IllegalArgumentException if { @code userEmail} is { @literal null or empty string, or TeamInfo not found}.
   */
  def findByUserEmail(@Param("user_email") userEmail: String): Optional[TeamInfo]

}
