/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

import java.time.LocalDate

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.ParticipationStatusGroupedByDayResults
import com.microsoft.graphdataconnect.watercooler.common.util.MeetingStatusUtils

case class ParticipationStatusGroupedByDayResultsResponse(day: LocalDate, participationStatus: String, counts: Int)

object ParticipationStatusGroupedByDayResultsResponse {

  def from(p: ParticipationStatusGroupedByDayResults): ParticipationStatusGroupedByDayResultsResponse = {
    ParticipationStatusGroupedByDayResultsResponse(p.getDay.toLocalDate,
      MeetingStatusUtils.participationStatusToString(Some(p.getParticipationStatus)),
      p.getCounts)
  }
}
