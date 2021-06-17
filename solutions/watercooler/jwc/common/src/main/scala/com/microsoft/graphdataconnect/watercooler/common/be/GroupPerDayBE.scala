/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.be

import java.time.LocalDateTime

case class GroupPerDayBE(id: String, day: LocalDateTime, hourTimeSlot: LocalDateTime, hour: Int, groupName: String, displayName: String, groupMembers: Map[String, GroupPerDayMemberBE])
