/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.be

import com.fasterxml.jackson.annotation.JsonProperty

case class GroupPerWeekMemberBE(@JsonProperty("mail") mail: String,
                                @JsonProperty("name") name: String)
