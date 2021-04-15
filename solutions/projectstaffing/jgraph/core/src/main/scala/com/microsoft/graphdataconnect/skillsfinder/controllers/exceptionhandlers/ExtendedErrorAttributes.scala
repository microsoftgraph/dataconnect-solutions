package com.microsoft.graphdataconnect.skillsfinder.controllers.exceptionhandlers

import java.util

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest

@Component
class ExtendedErrorAttributes extends DefaultErrorAttributes {

  override def getErrorAttributes(webRequest: WebRequest, options: ErrorAttributeOptions): util.Map[String, AnyRef] = {
    val errorAttributes: util.Map[String, AnyRef] = super.getErrorAttributes(webRequest, options)
    val correlationId = webRequest.getAttribute("correlationId", 0)
    errorAttributes.put("correlationId", correlationId)
    errorAttributes
  }
}
