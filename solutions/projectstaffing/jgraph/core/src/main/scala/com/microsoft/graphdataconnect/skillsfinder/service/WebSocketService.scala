package com.microsoft.graphdataconnect.skillsfinder.service

import com.microsoft.graphdataconnect.skillsfinder.config.WebSocketConfig
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.stereotype.Service

@Service
class WebSocketService(@Autowired val webSocketServer: SimpMessagingTemplate,
                       @Autowired val simpUserRegistry: SimpUserRegistry) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[WebSocketService])

  def emitNotification(path: String, message: AnyRef): Unit = synchronized {
    val dst = s"${WebSocketConfig.queuePrefix}/$path"
    logger.debug(s"Notifying subscribed clients with message '$message' on '$dst' channel")

    webSocketServer.convertAndSend(dst, message)
  }

}

object WebSocketService {
  val INGESTION_MODE_SWITCH_STATE_CHANNEL_PATH: String = "ingestion-mode-switch-state"
  val INGEST_HR_DATA_STATE_CHANNEL_PATH: String = "import-hr-data-state"
}
