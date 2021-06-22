/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.week

import java.time.LocalDateTime

import com.fasterxml.jackson.annotation.{JsonFormat, JsonProperty}
import com.microsoft.graphdataconnect.watercooler.common.be.GroupPerWeekBE
import org.springframework.format.annotation.DateTimeFormat

case class GroupPerWeekResponse(@JsonProperty("group_name") group_name: String,
                                @JsonProperty("tzinfo") tzinfo: String,
                                @JsonProperty("display_name") display_name: String,
                                @JsonProperty("hour") hour: Int,

                                hour_time_slot: LocalDateTime,
                                @JsonProperty("members") members: Array[GroupPerWeekMemberResponse]
                               ) {



}

object GroupPerWeekResponse {
  def fromBE(be: GroupPerWeekBE): GroupPerWeekResponse = {
    GroupPerWeekResponse (
      group_name = be.groupName,
      tzinfo = be.tzinfo,
      display_name = be.displayName,
      hour = be.hour,
      hour_time_slot = be.hourTimeSlot,
      members = be.members.map(x => GroupPerWeekMemberResponse.fromBE(x))
    )
  }
}

