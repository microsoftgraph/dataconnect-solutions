/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.entities.group

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.GroupPerWeekIdentity
import javax.persistence.{Column, EmbeddedId, Entity, Id, Table}

@Table(name = "groups_per_week")
@Entity
class GroupPerWeek {

  @EmbeddedId
  var composedId: GroupPerWeekIdentity = _

  @Column(name = "day", columnDefinition = "TIMESTAMP", nullable = false)
  var day: LocalDateTime = _

  @Column(name = "group_members", nullable = false)
  var groupMembers: String = _

  @Column(name = "timezone_str", nullable = false)
  var timezoneStr: String = _

  @Column(name = "timezone_nr", nullable = false)
  var timezoneNr: String = _

}
