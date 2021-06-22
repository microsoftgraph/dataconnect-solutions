/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.AvgGroupCountsInInterval

case class AvgGroupCountsInIntervalResponse(avgGroupCounts: Int)

object AvgGroupCountsInIntervalResponse {
  def from(p: AvgGroupCountsInInterval): AvgGroupCountsInIntervalResponse = {
    AvgGroupCountsInIntervalResponse(p.getAvggroupcounts)
  }
}

