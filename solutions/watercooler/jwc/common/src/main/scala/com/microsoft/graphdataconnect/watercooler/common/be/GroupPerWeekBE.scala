/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.be

import java.time.LocalDateTime

import com.fasterxml.jackson.annotation.JsonProperty

case class GroupPerWeekBE(@JsonProperty("group_name") groupName: String,
                          @JsonProperty("tzinfo") tzinfo: String,
                          @JsonProperty("display_name") displayName: String,
                          @JsonProperty("hour") hour: Int,
                          @JsonProperty("hour_time_slot") hourTimeSlot: LocalDateTime,
                          @JsonProperty("members") members: Array[GroupPerWeekMemberBE])