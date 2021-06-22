/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.util

object MeetingStatusUtils {

  def statusToString(numericStatus: Option[Int]) = {
    numericStatus match {
      case Some(v) => v match {
        case 0 => "rejected"
        case 1 => "accepted"
        case 2 => "unknown"
      }
      case None => "unknown"
    }
  }

  def participationStatusToString(numericStatus: Option[Int]) = {
    numericStatus match {
      case Some(v) => v match {
        case 0 => "not_participated"
        case 1 => "participated"
        case 2 => "unknown"
      }
      case None => "unknown"
    }
  }

}
