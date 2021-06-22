/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

import com.microsoft.graphdataconnect.watercooler.common.db.models.statistics.AvgMinMaxGroupSize

case class AvgMinMaxGroupSizeResponse(avgGroupSize:Integer,maxGroupSize:Integer,minGroupSize:Integer)

object AvgMinMaxGroupSizeResponse {
  def from(p:AvgMinMaxGroupSize):AvgMinMaxGroupSizeResponse = {
    AvgMinMaxGroupSizeResponse(p.getAvggroupsize,p.getMaxgroupsize,p.getMingroupsize)
  }
}