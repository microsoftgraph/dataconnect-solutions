/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.SelectedStatusCounts
import com.microsoft.graphdataconnect.watercooler.common.util.MeetingStatusUtils

case class SelectedStatusCountsResponse(selectedStatus: String, counts: Int)

object SelectedStatusCountsResponse {

  def from(p: SelectedStatusCounts): SelectedStatusCountsResponse = {
    SelectedStatusCountsResponse(MeetingStatusUtils.statusToString(Some(p.getSelectedStatus)), p.getCounts)
  }

}