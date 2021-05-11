/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.team

import com.microsoft.graphdataconnect.skillsfinder.utils.StringUtils.StringImplicits


case class TeamMemberRequest(employeeId: String,
                             email: String,
                             name: String,
                             skills: List[String]) {

  def validate(): Unit = {
    require(!employeeId.isNullOrBlank, "employeeId cannot be null or empty")
    require(!email.isNullOrBlank, "email cannot be null or empty")
    require(!name.isNullOrBlank, "name cannot be null or empty")
  }

}
