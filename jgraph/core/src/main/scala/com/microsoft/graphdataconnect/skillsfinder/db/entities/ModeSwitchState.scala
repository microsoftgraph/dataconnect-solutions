package com.microsoft.graphdataconnect.skillsfinder.db.entities

import javax.persistence._

// TODO keep this entity in sync with the default values inserted in db.py during deployment
@Table(name = "ingestion_mode_switch_state")
@Entity
class ModeSwitchState {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", insertable = false, nullable = false)
  var id: Long = _

  @Column(name = "ingestion_mode")
  var ingestionMode: String = _

  @Column(name = "phase")
  var phase: String = _

  @Column(name = "paused")
  var paused: Boolean = _

  @Column(name = "requester")
  var requester: String = _

  @Column(name = "start_time")
  var startTime: String = _

  @Column(name = "error_message", columnDefinition = "NVARCHAR")
  var errorMessage: String = _

  @Column(name = "error_stack_trace", columnDefinition = "NVARCHAR")
  var errorStackTrace: String = _

  @Column(name = "logs_correlation_id")
  var logsCorrelationId: String = _
}
