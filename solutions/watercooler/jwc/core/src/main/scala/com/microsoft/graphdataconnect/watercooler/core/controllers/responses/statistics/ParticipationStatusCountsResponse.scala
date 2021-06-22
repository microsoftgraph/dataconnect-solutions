/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.AvgMinMaxGroupSize
import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.ParticipationStatusCounts
import com.microsoft.graphdataconnect.watercooler.common.util.MeetingStatusUtils

case class ParticipationStatusCountsResponse(selectedStatus: String, counts: Int)

object ParticipationStatusCountsResponse {

  def from(p: ParticipationStatusCounts): ParticipationStatusCountsResponse = {
    ParticipationStatusCountsResponse(MeetingStatusUtils.participationStatusToString(Some(p.getParticipationStatus)), p.getCounts)
  }

}



