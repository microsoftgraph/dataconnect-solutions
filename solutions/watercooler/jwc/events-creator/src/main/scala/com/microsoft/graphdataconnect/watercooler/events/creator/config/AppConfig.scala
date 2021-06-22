/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator.config

import java.text.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


object AppConfig {

  val objectMapperBuilder: Jackson2ObjectMapperBuilder = {
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

}
