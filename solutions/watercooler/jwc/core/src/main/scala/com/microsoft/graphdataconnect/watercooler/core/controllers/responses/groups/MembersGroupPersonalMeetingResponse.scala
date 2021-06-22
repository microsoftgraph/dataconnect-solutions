/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups

case class MembersGroupPersonalMeetingResponse(id: String, groupName: String, groupBusySlots: Map[String, Object])
