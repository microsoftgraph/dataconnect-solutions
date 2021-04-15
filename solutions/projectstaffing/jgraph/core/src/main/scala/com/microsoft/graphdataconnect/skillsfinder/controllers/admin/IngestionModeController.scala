package com.microsoft.graphdataconnect.skillsfinder.controllers.admin

import com.microsoft.graphdataconnect.model.admin.IngestionMode
import com.microsoft.graphdataconnect.model.admin.IngestionMode.IngestionMode
import com.microsoft.graphdataconnect.model.admin.ModeSwitchRequestResolution.{Accepted, Rejected}
import com.microsoft.graphdataconnect.model.admin.ModeSwitchRequestType.{Resume, Retry, Switch}
import com.microsoft.graphdataconnect.skillsfinder.exceptions._
import com.microsoft.graphdataconnect.skillsfinder.models.TokenScope
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.{IngestionModeRequest, IngestionModeResponse}
import com.microsoft.graphdataconnect.skillsfinder.service.UserService
import com.microsoft.graphdataconnect.skillsfinder.service.ingestionmode.{IngestionModeSwitchAuditService, IngestionModeSwitchService, ModeSwitchStateService}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.CannotAcquireLockException
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

import scala.concurrent.ExecutionContext


@Controller
@RequestMapping(Array("/gdc/admin/ingestion-mode"))
class IngestionModeController(@Autowired val modeSwitchStateService: ModeSwitchStateService,
                              @Autowired val ingestionModeSwitchService: IngestionModeSwitchService,
                              @Autowired val switchAuditService: IngestionModeSwitchAuditService,
                              @Autowired val userService: UserService,
                              @Autowired implicit val executionContext: ExecutionContext) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[IngestionModeController])

  @GetMapping()
  def getIngestionMode(): ResponseEntity[IngestionModeResponse] = {
    val currentIngestionModeOpt: Option[IngestionMode.Value] = modeSwitchStateService.getIngestionMode
    logger.debug(s"Asked for current ingestion mode. Responding with $currentIngestionModeOpt")

    val currentIngestionMode = currentIngestionModeOpt.getOrElse(throw new ResourceNotFoundException("There is no Ingestion Mode set"))
    ResponseEntity.status(HttpStatus.OK).body(IngestionModeResponse(mode = currentIngestionMode))
  }

  @PutMapping()
  def updateIngestionMode(@RequestHeader httpHeaders: HttpHeaders,
                          @CookieValue(value = "AppServiceAuthSession") authSessionCookie: String,
                          @RequestBody ingestionModeRequest: IngestionModeRequest,
                          @RequestAttribute("userId") userId: String): ResponseEntity[IngestionModeResponse] = {
    try {
      try {
        if (userService.isCurrentUserAnAdmin(httpHeaders)) {
          val userToken = userService.getUserToken(httpHeaders, authSessionCookie, TokenScope.AZURE_MANAGEMENT_SCOPE)
          val currentIngestionMode: IngestionMode = ingestionModeSwitchService.updateIngestionMode(ingestionModeRequest.mode, userId)(userToken)
          switchAuditService.addAuditLog(Switch, ingestionModeRequest.mode, userId, Accepted)
          ResponseEntity.status(HttpStatus.OK).body(IngestionModeResponse(mode = currentIngestionMode))
        } else {
          throw new UnauthorizedException("User doesn't have admin privileges!")
        }
      } catch {
        case permissionException: PermissionConsentMissingException =>
          logger.error("Ingestion mode switch failed.", permissionException)
          ResponseEntity.status(HttpStatus.BAD_REQUEST).body(IngestionModeResponse(mode = null, permissionConsentLink = permissionException.consentRequestUrl))
        case e: CannotAcquireLockException =>
          val message = "Ingestion mode switch is already in progress"
          logger.error(message, e)
          throw new InvalidRequestException(message)
        case e: IngestionModeSwitchRejectedException =>
          logger.info(e.getMessage)
          switchAuditService.addAuditLog(Switch, ingestionModeRequest.mode, userId, Rejected, Some(e.getMessage))
          ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(IngestionModeResponse(ingestionModeRequest))
      }
    } catch {
      case e: Throwable =>
        switchAuditService.addAuditLog(Switch, ingestionModeRequest.mode, userId, Rejected, Some(e.getMessage))
        throw e
    }
  }

  @PutMapping(Array("/switch/retry"))
  def retryIngestionModeSwitch(@RequestHeader httpHeaders: HttpHeaders,
                               @CookieValue(value = "AppServiceAuthSession") authSessionCookie: String,
                               @RequestAttribute("userId") userId: String): ResponseEntity[IngestionModeResponse] = {
    try {
      try {
        if (userService.isCurrentUserAnAdmin(httpHeaders)) {
          val userToken = userService.getUserToken(httpHeaders, authSessionCookie, TokenScope.AZURE_MANAGEMENT_SCOPE)
          val currentIngestionMode: IngestionMode = ingestionModeSwitchService.retryIngestionModeSwitch(userId)(userToken)
          switchAuditService.addAuditLog(Retry, currentIngestionMode, userId, Accepted)
          ResponseEntity.status(HttpStatus.OK).body(IngestionModeResponse(mode = currentIngestionMode))
        } else {
          throw new UnauthorizedException("User doesn't have admin privileges!")
        }
      } catch {
        case permissionException: PermissionConsentMissingException =>
          logger.error("Ingestion mode switch failed.", permissionException)
          ResponseEntity.status(HttpStatus.BAD_REQUEST).body(IngestionModeResponse(mode = null, permissionConsentLink = permissionException.consentRequestUrl))
        case e: CannotAcquireLockException =>
          val message = "Ingestion mode switch is already in progress"
          logger.error(message, e)
          throw new InvalidRequestException(message)
      }
    } catch {
      case e: Throwable =>
        switchAuditService.addAuditLog(Retry, None, userId, Rejected, Some(e.getMessage))
        throw e
    }
  }

  @PutMapping(Array("/switch/resume"))
  def resumeIngestionModeSwitch(@RequestHeader httpHeaders: HttpHeaders,
                                @CookieValue(value = "AppServiceAuthSession") authSessionCookie: String,
                                @RequestAttribute("userId") userId: String): ResponseEntity[IngestionModeResponse] = {
    try {
      try {
        if (userService.isCurrentUserAnAdmin(httpHeaders)) {
          val userToken = userService.getUserToken(httpHeaders, authSessionCookie, scope = TokenScope.AZURE_MANAGEMENT_SCOPE)
          val currentIngestionMode: IngestionMode = ingestionModeSwitchService.resumeIngestionModeSwitch(userId)(userToken)
          switchAuditService.addAuditLog(Resume, currentIngestionMode, userId, Accepted)
          ResponseEntity.status(HttpStatus.OK).body(IngestionModeResponse(mode = currentIngestionMode))
        } else {
          throw new UnauthorizedException("User doesn't have admin privileges!")
        }
      } catch {
        case permissionException: PermissionConsentMissingException =>
          logger.error("Ingestion mode switch failed.", permissionException)
          ResponseEntity.status(HttpStatus.BAD_REQUEST).body(IngestionModeResponse(mode = null, permissionConsentLink = permissionException.consentRequestUrl))
        case e: CannotAcquireLockException =>
          val message = "Ingestion mode switch is already in progress"
          logger.error(message, e)
          throw new InvalidRequestException(message)
      }
    } catch {
      case e: Throwable =>
        switchAuditService.addAuditLog(Resume, None, userId, Rejected, Some(e.getMessage))
        throw e
    }
  }

}
