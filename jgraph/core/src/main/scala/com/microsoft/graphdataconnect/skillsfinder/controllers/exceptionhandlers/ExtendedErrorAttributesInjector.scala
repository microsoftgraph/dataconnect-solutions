package com.microsoft.graphdataconnect.skillsfinder.controllers.exceptionhandlers

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.slf4j.MDC
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver

@ControllerAdvice
class ExtendedErrorAttributesInjector extends DefaultHandlerExceptionResolver {

  override def doResolveException(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception): ModelAndView = {
    val correlationId = MDC.get("correlationId")
    request.setAttribute("correlationId", correlationId)
    throw ex
  }

}
