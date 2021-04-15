package com.microsoft.graphdataconnect.skillsfinder.controllers.exceptionhandlers

import java.sql.SQLException

import com.microsoft.graphdataconnect.skillsfinder.controllers.GdcResponse
import com.microsoft.graphdataconnect.skillsfinder.exceptions._
import org.hibernate.HibernateException
import org.slf4j.{Logger, LoggerFactory, MDC}
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{ExceptionHandler, RestControllerAdvice}
import org.springframework.web.context.request.{ServletWebRequest, WebRequest}

@RestControllerAdvice
class ControllerExceptionHandler {
  private val log: Logger = LoggerFactory.getLogger(classOf[ControllerExceptionHandler])

  @ExceptionHandler(Array(classOf[ServerErrorException]))
  def handleServerErrorException(ex: ServerErrorException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message = ex.getMessage,
      error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[ServiceUnavailableException]))
  def handleServiceUnavailableException(ex: ServiceUnavailableException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.SERVICE_UNAVAILABLE.value(),
      message = ex.getMessage,
      error = HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[InvalidRequestException]))
  def handleInvalidRequestException(ex: InvalidRequestException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.BAD_REQUEST.value(),
      message = ex.getMessage,
      error = HttpStatus.BAD_REQUEST.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[ResourceNotFoundException]))
  def handleInvalidRequestException(ex: ResourceNotFoundException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.NOT_FOUND).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.NOT_FOUND.value(),
      message = ex.getMessage,
      error = HttpStatus.NOT_FOUND.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[UnauthorizedException]))
  def handleUnauthorizedException(ex: UnauthorizedException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.UNAUTHORIZED.value(),
      message = ex.getMessage,
      error = HttpStatus.UNAUTHORIZED.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[GatewayTimeoutException]))
  def handleUnauthorizedException(ex: GatewayTimeoutException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.BAD_GATEWAY.value(),
      message = ex.getMessage,
      error = HttpStatus.BAD_GATEWAY.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[DataIntegrityViolationException]))
  def handleDataIntegrityViolationException(ex: DataIntegrityViolationException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message = "Data integrity exception. Report this error to GDC support.",
      error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[SQLException]))
  def handleSQLException(ex: SQLException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message = "SQL exception. Report this error to GDC support.",
      error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[HibernateException]))
  def handleHibernateException(ex: HibernateException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message = "Database exception. Report this error to GDC support.",
      error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[AzureSearchIndexConfigurationMissing]))
  def handleAzureSearchIndexConfigurationMissing(ex: AzureSearchIndexConfigurationMissing, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message = ex.getMessage,
      error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[AzureSearchIndexNotFound]))
  def handleAzureSearchIndexNotFound(ex: AzureSearchIndexNotFound, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message = ex.getMessage,
      error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[IllegalArgumentException]))
  def handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.BAD_REQUEST.value(),
      message = ex.getMessage,
      error = HttpStatus.BAD_REQUEST.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[ComposedPKVersionNotFoundException]))
  def handleComposedPKVersionNotFoundException(ex: ComposedPKVersionNotFoundException, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message = ex.getMessage,
      error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

  @ExceptionHandler(Array(classOf[Throwable]))
  def handleThrowable(ex: Throwable, request: WebRequest): ResponseEntity[AnyRef] = {
    log.error(ex.getMessage)
    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GdcResponse(timestamp = System.currentTimeMillis,
      status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
      message = ex.getMessage,
      error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase,
      path = request.asInstanceOf[ServletWebRequest].getRequest().getRequestURI(),
      correlationId = MDC.get("correlationId")))
  }

}
