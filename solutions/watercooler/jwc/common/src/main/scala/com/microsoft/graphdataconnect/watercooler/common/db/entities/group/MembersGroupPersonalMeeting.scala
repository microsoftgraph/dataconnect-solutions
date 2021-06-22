/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.entities.group

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.MembersGroupPersonalMeetingIdentity
import javax.persistence.{Column, EmbeddedId, Entity, Id, Table}


@Table(name = "members_group_personal_meetings")
@Entity
class MembersGroupPersonalMeeting {

  @EmbeddedId
  var composedId: MembersGroupPersonalMeetingIdentity = _

  @Column(name = "group_name", nullable = false)
  var groupName: String = _

  @Column(name = "watercooler_hour")
  var hour: Int = _

  @Column(name = "group_busy_slots", nullable = false)
  var groupBusySlots: String = _

  @Column(name = "timezone_str", nullable = false)
  var timezoneStr: String = _

  @Column(name = "timezone_nr", nullable = false)
  var timezoneNr: String = _
}
