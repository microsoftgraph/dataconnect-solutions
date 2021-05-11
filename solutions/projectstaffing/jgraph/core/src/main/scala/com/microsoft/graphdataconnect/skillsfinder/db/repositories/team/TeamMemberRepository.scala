/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.team

import com.microsoft.graphdataconnect.skillsfinder.db.entities.team.TeamMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait TeamMemberRepository extends JpaRepository[TeamMember, Long] {

  def findByOwnerEmail(@Param("owner_email") ownerEmail: String): java.util.List[TeamMember]

}
