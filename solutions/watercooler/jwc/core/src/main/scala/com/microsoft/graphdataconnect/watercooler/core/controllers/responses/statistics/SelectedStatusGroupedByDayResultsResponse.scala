/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

import java.time.LocalDate

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.SelectedStatusGroupedByDayResults
import com.microsoft.graphdataconnect.watercooler.common.util.MeetingStatusUtils

case class SelectedStatusGroupedByDayResultsResponse(day: LocalDate, selectedStatus: String, counts: Int)

object SelectedStatusGroupedByDayResultsResponse {

  def from(p: SelectedStatusGroupedByDayResults): SelectedStatusGroupedByDayResultsResponse = {
    SelectedStatusGroupedByDayResultsResponse(p.getDay.toLocalDate,
      MeetingStatusUtils.statusToString(Some(p.getSelectedStatus)),
      p.getCounts)
  }

}
