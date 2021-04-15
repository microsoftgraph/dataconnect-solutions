package com.microsoft.graphdataconnect.skillsfinder.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig() extends WebSocketMessageBrokerConfigurer {

  override def configureMessageBroker(registry: MessageBrokerRegistry): Unit = {
    registry.enableSimpleBroker(WebSocketConfig.queuePrefix)
    registry.setApplicationDestinationPrefixes(WebSocketConfig.appPrefix)
  }

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit = {
    registry.addEndpoint(WebSocketConfig.topicPrefix).setAllowedOrigins("*")
  }

}

object WebSocketConfig {
  val appPrefix = "/app"
  val queuePrefix = "/queue"
  val topicPrefix = "/gdc-topic"
}
