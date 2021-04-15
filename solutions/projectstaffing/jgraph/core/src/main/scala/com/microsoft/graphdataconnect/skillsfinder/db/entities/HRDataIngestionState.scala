package com.microsoft.graphdataconnect.skillsfinder.db.entities

import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.HRDataIngestionStateModel
import com.microsoft.graphdataconnect.utils.TimeUtils
import javax.persistence._

@Table(name = "hr_data_ingestion_state")
@Entity
class HRDataIngestionState {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", insertable = false, nullable = false)
  var id: Long = _

  @Column(name = "phase")
  var phase: String = _

  @Column(name = "requester")
  var requester: String = _

  @Column(name = "start_time")
  var start_time: String = _

  @Column(name = "error_message", columnDefinition = "NVARCHAR")
  var error_message: String = _

  @Column(name = "error_stack_trace", columnDefinition = "NVARCHAR")
  var error_stack_trace: String = _

  @Column(name = "logs_correlation_id")
  var logs_correlation_id: String = _

}

object HRDataIngestionState {

  def apply(hrDataIngestionStateModel: HRDataIngestionStateModel): HRDataIngestionState = {
    val newHRDataIngestionState = new HRDataIngestionState()
    newHRDataIngestionState.phase = hrDataIngestionStateModel.phase.toString
    newHRDataIngestionState.start_time = TimeUtils.zonedDateTimeToTimestampString(hrDataIngestionStateModel.startTime)
    newHRDataIngestionState.requester = hrDataIngestionStateModel.requester
    newHRDataIngestionState.error_message = hrDataIngestionStateModel.errorMessage
    newHRDataIngestionState.error_stack_trace = hrDataIngestionStateModel.errorStackTrace
    newHRDataIngestionState.logs_correlation_id = hrDataIngestionStateModel.logsCorrelationId
    newHRDataIngestionState
  }

}
