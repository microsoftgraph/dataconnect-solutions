/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.entities

import javax.persistence._

object IngestionModeSwitchLog {
  def apply(requestType: String,
            targetIngestionMode: String,
            requester: String,
            requestTime: String,
            requestResolution: String,
            logsCorrelationId: String,
            message: String): IngestionModeSwitchLog = {
    val switchLog = new IngestionModeSwitchLog
    switchLog.requestType = requestType
    switchLog.targetIngestionMode = targetIngestionMode
    switchLog.requester = requester
    switchLog.requestTime = requestTime
    switchLog.requestResolution = requestResolution
    switchLog.logsCorrelationId = logsCorrelationId
    switchLog.message = message

    switchLog
  }
}

@Table(name = "ingestion_mode_switch_audit")
@Entity
class IngestionModeSwitchLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", insertable = false, nullable = false)
  var id: Long = _

  @Column(name = "request_type")
  var requestType: String = _

  @Column(name = "target_ingestion_mode")
  var targetIngestionMode: String = _

  @Column(name = "requester")
  var requester: String = _

  @Column(name = "request_time")
  var requestTime: String = _

  @Column(name = "request_resolution")
  var requestResolution: String = _

  @Column(name = "logs_correlation_id")
  var logsCorrelationId: String = _

  @Column(name = "message", columnDefinition = "NVARCHAR")
  var message: String = _
}
