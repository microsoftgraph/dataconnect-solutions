/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import java.time.ZonedDateTime

import com.microsoft.graphdataconnect.model.admin.HRDataIngestionPhase
import com.microsoft.graphdataconnect.model.admin.HRDataIngestionPhase.HRDataIngestionPhase
import com.microsoft.graphdataconnect.skillsfinder.db.entities.HRDataIngestionState
import com.microsoft.graphdataconnect.utils.TimeUtils

case class HRDataIngestionStateModel(phase: HRDataIngestionPhase,
                                     requester: String,
                                     startTime: ZonedDateTime,
                                     errorMessage: String,
                                     errorStackTrace: String,
                                     logsCorrelationId: String)

object HRDataIngestionStateModel {
  def apply(hrDataIngestionState: HRDataIngestionState): HRDataIngestionStateModel = {
    HRDataIngestionStateModel(
      phase = HRDataIngestionPhase.withCaseInsensitiveName(hrDataIngestionState.phase).getOrElse(HRDataIngestionPhase.Error),
      requester = hrDataIngestionState.requester,
      startTime = TimeUtils.timestampStringToZonedDateTime(hrDataIngestionState.start_time),
      errorMessage = hrDataIngestionState.error_message,
      errorStackTrace = hrDataIngestionState.error_stack_trace,
      logsCorrelationId = hrDataIngestionState.logs_correlation_id
    )
  }

  lazy val default: HRDataIngestionStateModel = {
    HRDataIngestionStateModel(
      phase = HRDataIngestionPhase.EmployeePipelineRunFinished,
      requester = null,
      startTime = TimeUtils.oldestSqlDateZDT,
      errorMessage = null,
      errorStackTrace = null,
      logsCorrelationId = null
    )
  }
}
