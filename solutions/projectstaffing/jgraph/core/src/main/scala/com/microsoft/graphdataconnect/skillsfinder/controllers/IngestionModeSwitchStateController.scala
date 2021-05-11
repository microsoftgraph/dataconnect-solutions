/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.model.admin.IngestionModeSwitchPhase.IngestionModeSwitchPhase
import com.microsoft.graphdataconnect.skillsfinder.exceptions.ResourceNotFoundException
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.IngestionModeSwitchPhaseDTO
import com.microsoft.graphdataconnect.skillsfinder.models.response
import com.microsoft.graphdataconnect.skillsfinder.models.response.IngestionModeSwitchStateResponse
import com.microsoft.graphdataconnect.skillsfinder.service.WebSocketService
import com.microsoft.graphdataconnect.skillsfinder.service.ingestionmode.ModeSwitchStateService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping}

@Controller()
@RequestMapping(Array("/gdc/ingestion-mode-switch-state"))
class IngestionModeSwitchStateController(@Autowired val webSocketService: WebSocketService,
                                         @Autowired val modeSwitchStateService: ModeSwitchStateService) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[IngestionModeSwitchStateController])

  @GetMapping(Array("/phase"))
  def getCurrentIngestionModeSwitchPhase(): ResponseEntity[IngestionModeSwitchPhaseDTO] = {
    val currentIngestionModeSwitchPhaseOpt: Option[IngestionModeSwitchPhase] = modeSwitchStateService.getIngestionModeSwitchPhase
    logger.debug(s"Asked for current ingestion mode switch phase. Responding with $currentIngestionModeSwitchPhaseOpt")

    val currentIngestionModeSwitchPhase = currentIngestionModeSwitchPhaseOpt.getOrElse(throw new ResourceNotFoundException("There is no Ingestion Mode Switch Phase set"))
    ResponseEntity.status(HttpStatus.OK).body(IngestionModeSwitchPhaseDTO(phase = currentIngestionModeSwitchPhase))
  }


  @GetMapping()
  def getCurrentIngestionModeSwitchState(): ResponseEntity[IngestionModeSwitchStateResponse] = {
    val ingestionModeSwitchState = response.IngestionModeSwitchStateResponse(modeSwitchStateService.getIngestionModeSwitchState)
    logger.debug(s"Asked for current ingestion mode switch state. Responding with $ingestionModeSwitchState")

    ResponseEntity.status(HttpStatus.OK).body(ingestionModeSwitchState)
  }


}
