/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.config

import java.text.DateFormat
import java.time.LocalDateTime

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.filter.CommonsRequestLoggingFilter
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.time.format.DateTimeFormatter

@Configuration
@EnableScheduling
class AppConfig(@Autowired environment: Environment) {

  @Primary
  @Bean
  def objectMapperBuilder: Jackson2ObjectMapperBuilder = {
    val builder = new Jackson2ObjectMapperBuilder
    builder.serializationInclusion(JsonInclude.Include.NON_NULL)
    builder.dateFormat(DateFormat.getDateInstance)
    builder.modulesToInstall(classOf[DefaultScalaModule])

    val module = new JavaTimeModule
    module.addSerializer(classOf[LocalDateTime], new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
    module.addDeserializer(classOf[LocalDateTime], new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
    builder.modules(module)
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

}
