package com.microsoft.graphdataconnect.skillsfinder.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration, Primary}
import org.springframework.core.env.Environment
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.filter.CommonsRequestLoggingFilter

import scala.concurrent.ExecutionContext

@Configuration
@EnableScheduling
class AppConfig(@Autowired environment: Environment) {

  @Primary
  @Bean
  def objectMapperBuilder: Jackson2ObjectMapperBuilder = {
    val builder = new Jackson2ObjectMapperBuilder
    builder.serializationInclusion(JsonInclude.Include.NON_NULL)
    builder.modulesToInstall(classOf[JavaTimeModule], classOf[DefaultScalaModule])
    builder
  }

  @Bean
  def requestLoggingFilter: CommonsRequestLoggingFilter = {
    val loggingFilter = new CommonsRequestLoggingFilter
    loggingFilter.setIncludeClientInfo(true)
    loggingFilter.setIncludeQueryString(true)
    loggingFilter.setIncludePayload(true)
    loggingFilter.setIncludeHeaders(true)
    loggingFilter.setMaxPayloadLength(64000)
    loggingFilter
  }

  @Bean
  def executionContext: ExecutionContext = {
    ExecutionContext.global
  }
}
