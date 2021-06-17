/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.config

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduleService {
  private val log: Logger = LoggerFactory.getLogger(classOf[ScheduleService])

  @Autowired
  var configurationService: ConfigurationService = _

  @Scheduled(fixedRateString = "${scheduler.delay.data}", initialDelayString = "${scheduler.delay.initial}")
  def checkEmployeeProfilesDataVersionConfiguration(): Unit = {
    log.trace("Checking for latest version of Employee profile data")
    val oldVersion = configurationService.getLatestEmployeeProfileDataVersionFromCache()
    val latestVersion: LocalDateTime = configurationService.getLatestEmployeeProfileDataVersionFromCache(refreshVersionConfigCache = true)
    if(!oldVersion.isEqual(latestVersion)) {
      log.info(s"Latest Employee Profiles date version is: {$latestVersion}")
    }
  }

  @Scheduled(fixedRateString = "${scheduler.delay.data}", initialDelayString = "${scheduler.delay.initial}")
  def checkGroupsDataVersionConfiguration(): Unit = {
    log.trace("Checking for latest version of Employee profile data")
    val oldVersion = configurationService.getLatestGroupsDataVersion()
    val latestVersion: LocalDateTime = configurationService.getLatestGroupsDataVersionFromCache(refreshVersionConfigCache = true)
    if(!oldVersion.isEqual(latestVersion)) {
      log.info(s"Latest Groups date version date is: {$latestVersion}")
    }
  }

}
