/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.entities.group

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.MembersToGroupParticipationIdentity
import javax.persistence.{Column, EmbeddedId, Entity, Id, Table}

@Table(name = "members_to_group_participation")
@Entity
class MembersToGroupParticipation {

  @EmbeddedId
  var composedId: MembersToGroupParticipationIdentity = _

  @Column(name = "group_name", nullable = false)
  var groupName: String = _

  @Column(name = "day", columnDefinition = "TIMESTAMP", nullable = false)
  var day: LocalDateTime = _

  @Column(name = "hour_time_slot", columnDefinition = "TIMESTAMP", nullable = false)
  var hourTimeSlot: LocalDateTime = _

  @Column(name = "member_email", nullable = false)
  var memberEmail: String = _

  @Column(name = "invitation_status")
  var selectedStatus: Int = _

  @Column(name = "participation_status")
  var participationStatus: Int = _

  @Column(name = "timezone_str", nullable = false)
  var timezoneStr: String = _

  @Column(name = "timezone_nr", nullable = false)
  var timezoneNr: String = _

}
