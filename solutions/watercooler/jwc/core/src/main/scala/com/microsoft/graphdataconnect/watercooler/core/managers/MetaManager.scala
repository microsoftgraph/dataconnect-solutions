/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.managers

import com.microsoft.graphdataconnect.watercooler.common.dto.DistinctTimezoneDTO
import com.microsoft.graphdataconnect.watercooler.common.services.group.GroupPerDayService
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class MetaManager(@Autowired groupPerDayService: GroupPerDayService,
                  @Autowired configurationService: ConfigurationService) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[MetaManager])

  def getAvailableTimezoneFilters(): List[DistinctTimezoneDTO] = {
    groupPerDayService.getDistinctTimezoneFilters()
  }

  def updateSettings(body: String): Unit = {
    configurationService.updateSettings(body)
  }

  def getSettings(): String = {
    configurationService.getSettings()
  }

}
