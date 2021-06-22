/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers

import com.microsoft.graphdataconnect.watercooler.common.dto.DistinctTimezoneDTO
import com.microsoft.graphdataconnect.watercooler.core.managers.MetaManager
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, PutMapping, RequestBody, RequestMapping, RestController}

@RestController
@RequestMapping(Array("/jwc/meta"))
class MetaController(@Autowired metaManager: MetaManager) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[MetaController])

  @GetMapping(Array("/available/timezone-filters"))
  def getBusySlotsPerGroup(): ResponseEntity[List[DistinctTimezoneDTO]] = {
    logger.info(s"Asked for available timezone filters")
    val result: List[DistinctTimezoneDTO] = metaManager.getAvailableTimezoneFilters()
    ResponseEntity.status(HttpStatus.OK).body(result)
  }

  @GetMapping(Array("/settings"))
  def getSettings(): ResponseEntity[String] = {
    logger.info(s"Asked for settings")
    val result = metaManager.getSettings()
    ResponseEntity.status(HttpStatus.OK).body(result)
  }

  @PutMapping(Array("/settings"))
  def updateSettings(@RequestBody body: String): ResponseEntity[Boolean] = {
    logger.info(s"Asked to update the settings")
    metaManager.updateSettings(body)
    ResponseEntity.status(HttpStatus.OK).body(true)
  }

}
