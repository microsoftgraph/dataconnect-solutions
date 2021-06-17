/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.models.statistics

import java.time.LocalDateTime

trait SelectedStatusGroupedByDayResults {
  def getDay: LocalDateTime
  def getSelectedStatus: Int
  def getCounts: Int
}






