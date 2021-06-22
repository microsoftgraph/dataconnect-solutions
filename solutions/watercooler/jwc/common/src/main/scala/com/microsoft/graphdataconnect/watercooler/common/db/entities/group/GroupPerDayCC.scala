/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.entities.group

case class GroupPerDayCC(
                          id: String,
                          version: String,
                          hour_time_slot: String,
                          hour: Int,
                          group_name: String,
                          display_name: String,
                          group_members: String,
                          timezone_str: String,
                          timezone_nr: String
                        ) {

}
