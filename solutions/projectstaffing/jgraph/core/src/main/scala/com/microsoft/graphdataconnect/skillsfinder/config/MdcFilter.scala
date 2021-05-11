/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.config

import java.io.IOException
import java.util.UUID

import com.microsoft.graphdataconnect.skillsfinder.exceptions.UnauthorizedException
import com.microsoft.graphdataconnect.skillsfinder.service.UserService
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{FilterChain, ServletException}
import org.slf4j.{Logger, LoggerFactory, MDC}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class MdcFilter(@Autowired override val userService: UserService) extends UserIdExtractionFilter(userService) {
  private val log: Logger = LoggerFactory.getLogger(classOf[MdcFilter])

  @throws[IOException]
  @throws[ServletException]
  override protected def doFilter(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain): Unit = {
    try {
      MDC.put("correlationId", getCorrelationId(request))
      val userId: Option[String] = getUserIdFromHttpServletRequest(request)

      if (userId.isDefined) {
        MDC.put("user", userId.get)
      }

    } catch {
      case _: UnauthorizedException => None
    }
    finally {
      try {
        // filterChain.doFilter call has to be made from this finally block,
        // otherwise if the filterChain.doFilter call would be in the try block above, if an exception is thrown from the try block
        // filterChain.doFilter might not get called thus the UserIdExtractionFilter.doFilter will not be called
        filterChain.doFilter(request, response)
      } finally {
        MDC.remove("correlationId")
        MDC.remove("user")
      }
    }
  }

  private def getCorrelationId(request: HttpServletRequest): String = {
    Option(request.getHeader("X-Correlation-ID")) match {
      case Some(existingCorrelationId) =>
        existingCorrelationId
      case None =>
        val newCorrelationId = UUID.randomUUID().toString
        newCorrelationId
    }
  }
}

object MdcUtil {
  def getCorrelationId(): String = {
    MDC.get("correlationId")
  }

  def setCorrelationId(correlationId: String) = {
    MDC.put("correlationId", correlationId)
  }

  def resetCorrelationId() = {
    MDC.remove("correlationId")
  }
}
