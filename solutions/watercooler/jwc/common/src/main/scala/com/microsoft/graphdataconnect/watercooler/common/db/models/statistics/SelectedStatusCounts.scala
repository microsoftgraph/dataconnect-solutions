/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.models.statistics

trait SelectedStatusCounts {
  def getSelectedStatus: Int
  def getCounts: Int
}
