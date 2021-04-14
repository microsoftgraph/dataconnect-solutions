package com.microsoft.graphdataconnect.skillsfinder.service.ingestionmode

import java.time.{ZoneOffset, ZonedDateTime}

import com.microsoft.graphdataconnect.model.admin.IngestionMode.IngestionMode
import com.microsoft.graphdataconnect.model.admin.ModeSwitchRequestResolution.ModeSwitchRequestResolution
import com.microsoft.graphdataconnect.model.admin.ModeSwitchRequestType.ModeSwitchRequestType
import com.microsoft.graphdataconnect.skillsfinder.config.MdcUtil
import com.microsoft.graphdataconnect.skillsfinder.db.entities.IngestionModeSwitchLog
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.IngestionModeSwitchAuditRepository
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IngestionModeSwitchAuditService(@Autowired val auditRepository: IngestionModeSwitchAuditRepository) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[IngestionModeSwitchAuditService])

  def addAuditLog(requestType: ModeSwitchRequestType,
                  targetIngestionMode: IngestionMode,
                  requester: String,
                  requestResolution: ModeSwitchRequestResolution,
                  message: Option[String] = None): Unit = {
    addAuditLog(requestType, Some(targetIngestionMode), requester, requestResolution, message)
  }

  def addAuditLog(requestType: ModeSwitchRequestType,
                  targetIngestionMode: Option[IngestionMode],
                  requester: String,
                  requestResolution: ModeSwitchRequestResolution,
                  message: Option[String]): Unit = {
    val timestampNow = ZonedDateTime.now(ZoneOffset.UTC)
    val requestTimeStr = TimeUtils.zonedDateTimeToTimestampString(timestampNow)
    val correlationId = MdcUtil.getCorrelationId()

    val modeSwitchLog = IngestionModeSwitchLog(
      requestType.toString,
      targetIngestionMode.map(_.toString).orNull,
      requester,
      requestTimeStr,
      requestResolution.toString,
      correlationId,
      message.orNull
    )

    try {
      auditRepository.save(modeSwitchLog)
      logger.info(s"Added record to ingestion mode switch audit table: $modeSwitchLog")
    } catch {
      case e: Throwable => logger.warn(s"Error while adding record to ingestion mode switch audit table: $modeSwitchLog", e)
    }
  }


}


