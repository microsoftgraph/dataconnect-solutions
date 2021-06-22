/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.repositories.group

import java.time.LocalDateTime
import java.util.Optional

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.MembersGroupPersonalMeeting
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.MembersGroupPersonalMeetingIdentity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait MembersGroupPersonalMeetingRepository extends JpaRepository[MembersGroupPersonalMeeting, MembersGroupPersonalMeetingIdentity]{

  def findFirstByGroupNameAndComposedIdVersion(@Param("groupName") groupName: String,
                                               @Param("version") version: LocalDateTime):Optional[MembersGroupPersonalMeeting]

}
