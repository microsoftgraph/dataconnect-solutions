/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.AvgParticipationPerGroupPerDayInInterval


case class AvgParticipationPerGroupPerDayInIntervalResponse(averageParticipationIngroup: Int)

object AvgParticipationPerGroupPerDayInIntervalResponse {

  def from(p: AvgParticipationPerGroupPerDayInInterval): AvgParticipationPerGroupPerDayInIntervalResponse = {
    AvgParticipationPerGroupPerDayInIntervalResponse(p.getAvgparticipationingroup)
  }

}

