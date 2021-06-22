/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.groups.week

import com.fasterxml.jackson.annotation.JsonProperty
import com.microsoft.graphdataconnect.watercooler.common.be.GroupPerWeekMemberBE

case class GroupPerWeekMemberResponse(@JsonProperty("mail") mail: String,
                                      @JsonProperty("name") name: String) {


}

object GroupPerWeekMemberResponse {
  def fromBE(be: GroupPerWeekMemberBE): GroupPerWeekMemberResponse = {
    GroupPerWeekMemberResponse(
      mail = be.mail,
      name = be.name
    )
  }
}
