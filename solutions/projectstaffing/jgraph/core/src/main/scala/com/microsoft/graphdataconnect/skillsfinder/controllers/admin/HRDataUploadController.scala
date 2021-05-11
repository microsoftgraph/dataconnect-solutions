/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.controllers.admin

import com.microsoft.graphdataconnect.skillsfinder.exceptions.{HRDataIngestionRejectedException, InvalidRequestException, PermissionConsentMissingException, UnauthorizedException}
import com.microsoft.graphdataconnect.skillsfinder.models.TokenScope
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.{HRDataIngestionStateModel, HRDataIngestionStateResponse, HRDataUploadResponse, UserToken}
import com.microsoft.graphdataconnect.skillsfinder.service.adf.ADFService
import com.microsoft.graphdataconnect.skillsfinder.service.{HRDataIngestionService, HRDataIngestionStateService, UserService}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.CannotAcquireLockException
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping(Array("/gdc/admin/hr-data-upload"))
class HRDataUploadController(@Autowired val userService: UserService,
                             @Autowired val hrDataIngestionService: HRDataIngestionService,
                             @Autowired val hrDataIngestionStateService: HRDataIngestionStateService,
                             @Autowired val adfService: ADFService) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[HRDataUploadController])

  @GetMapping(Array("/state"))
  def getHRDataIngestionState(): ResponseEntity[HRDataIngestionStateResponse] = {
    val currentHrDataIngestionStateOpt: Option[HRDataIngestionStateModel] = hrDataIngestionStateService.getHRDataIngestionState()

    val currentHRDataIngestionState = currentHrDataIngestionStateOpt.getOrElse(HRDataIngestionStateModel.default)

    ResponseEntity.ok().body(HRDataIngestionStateResponse(currentHRDataIngestionState.phase))
  }

  @PostMapping()
  def uploadHRDataFile(@RequestHeader httpHeaders: HttpHeaders,
                       @CookieValue(value = "AppServiceAuthSession") authSessionCookie: String,
                       @RequestParam("file") multipartFile: MultipartFile,
                       @RequestAttribute("userId") userId: String): ResponseEntity[HRDataUploadResponse] = {
    logger.info(s"Received HR Data upload request using a file of size ${multipartFile.getSize}")
    try {
      if (userService.isCurrentUserAnAdmin(httpHeaders)) {
        val storageUserToken: UserToken = userService.getUserToken(httpHeaders, authSessionCookie, scope = TokenScope.AZURE_STORAGE_SCOPE)
        val adfUserToken: UserToken = userService.getUserToken(httpHeaders, authSessionCookie, scope = TokenScope.AZURE_MANAGEMENT_SCOPE)
        hrDataIngestionService.startHRDataIngestion(userId, multipartFile, storageUserToken = storageUserToken, adfUserToken = adfUserToken)
        ResponseEntity.ok().body(HRDataUploadResponse(""))
      } else {
        throw new UnauthorizedException("User doesn't have admin privileges!")
      }

    } catch {
      case permissionException: PermissionConsentMissingException =>
        logger.error("Failed to upload the HR Data file", permissionException)
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HRDataUploadResponse(permissionException.consentRequestUrl))
      case e: CannotAcquireLockException =>
        val message = "HR data ingestion is already in progress"
        logger.error(message, e)
        throw new InvalidRequestException(message)
      case e: HRDataIngestionRejectedException =>
        logger.info(e.getMessage)
        //TODO add audit
        throw new InvalidRequestException(e.getMessage)
      case e: Throwable =>
        logger.error("Failed to upload HR Data csv file to Azure Blob Storage", e)
        throw e
    }
  }

}
