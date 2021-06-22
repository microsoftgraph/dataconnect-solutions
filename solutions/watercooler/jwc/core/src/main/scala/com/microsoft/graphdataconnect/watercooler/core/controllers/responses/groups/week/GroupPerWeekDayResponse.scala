/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.week

import com.fasterxml.jackson.annotation.JsonProperty

case class GroupPerWeekDayResponse(
                                    @JsonProperty("day") day: String,
                                    @JsonProperty("groups") groups: Array[GroupPerWeekResponse]
                               )
