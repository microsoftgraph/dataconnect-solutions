/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.services.meta

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.watercooler.common.db.entities.meta.ConfigurationTypes
import com.microsoft.graphdataconnect.watercooler.common.db.entities.meta.{Configuration, ConfigurationTypes}
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.meta.ConfigurationRepository
import com.microsoft.graphdataconnect.watercooler.common.util.{Constants, TimeUtils}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.{CachePut, Cacheable}
import org.springframework.stereotype.Service

import scala.compat.java8.OptionConverters._

@Service
class ConfigurationService(@Autowired configurationRepository: ConfigurationRepository) {

  private val log: Logger = LoggerFactory.getLogger(classOf[ConfigurationService])

  def getLatestEmployeeProfileDataVersion(): LocalDateTime = {
    configurationRepository.findById(ConfigurationTypes.LatestVersionOfEmployeeProfiles)
      .asScala
      .map { config: Configuration =>
        TimeUtils.timestampToLocalDateTime(config.value)
      }.getOrElse(TimeUtils.oldestSqlDate)
  }

  @Cacheable(value = Array(Constants.CACHE_NAME), condition = "#refreshVersionConfigCache==false", key = "#root.methodName")
  @CachePut(value = Array(Constants.CACHE_NAME), condition = "#refreshVersionConfigCache==true", key = "#root.methodName")
  def getLatestEmployeeProfileDataVersionFromCache(refreshVersionConfigCache: Boolean = false): LocalDateTime = {
    getLatestEmployeeProfileDataVersion()
  }

  def getLatestGroupsDataVersion(): LocalDateTime = {
    configurationRepository.findById(ConfigurationTypes.LatestVersionOfGroups)
      .asScala
      .map { config: Configuration =>
        TimeUtils.timestampToLocalDateTime(config.value)
      }.getOrElse(TimeUtils.oldestSqlDate)
  }

  @Cacheable(value = Array(Constants.CACHE_NAME), condition = "#refreshVersionConfigCache==false", key = "#root.methodName")
  @CachePut(value = Array(Constants.CACHE_NAME), condition = "#refreshVersionConfigCache==true", key = "#root.methodName")
  def getLatestGroupsDataVersionFromCache(refreshVersionConfigCache: Boolean = false): LocalDateTime = {
    getLatestGroupsDataVersion()
  }

  def updateSettings(body: String): Unit = {
    val settingsOpt = configurationRepository.findById(ConfigurationTypes.GlobalSettings)

    if (settingsOpt.isPresent) {
      val settings = settingsOpt.get()
      settings.value = body
      configurationRepository.save(settings)
    } else {
      val newSettings: Configuration = new Configuration
      newSettings.key = ConfigurationTypes.GlobalSettings
      newSettings.value = body
      configurationRepository.save(newSettings)
    }
  }

  def getSettings(): String = {
    val settingsOpt = configurationRepository.findById(ConfigurationTypes.GlobalSettings)
    if (settingsOpt.isPresent) {
      settingsOpt.get().value
    } else "{}"
  }

}
