/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.config

import java.io.IOException

import com.microsoft.graphdataconnect.watercooler.common.exceptions.UnauthorizedException
import com.microsoft.graphdataconnect.watercooler.common.services.meta.UserService
import javax.servlet.http.{HttpFilter, HttpServletRequest, HttpServletResponse}
import javax.servlet.{FilterChain, ServletException}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus

@Configuration
class UserIdExtractionFilter(@Autowired val userService: UserService) extends HttpFilter {
  private val log: Logger = LoggerFactory.getLogger(classOf[UserIdExtractionFilter])

  @throws[IOException]
  @throws[ServletException]
  override protected def doFilter(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain): Unit = {
    try {
      val userId: Option[String] = getUserIdFromHttpServletRequest(request)

      if (userId.isDefined) {
        request.setAttribute("userId", userId.get)
      } else {
        throw new UnauthorizedException("Authentication failed!")
      }
      filterChain.doFilter(request, response)
    } catch {
      case _: UnauthorizedException => {
        log.info("Received unauthorized request on " + request.getRequestURI + " endpoint.")
        response.setStatus(HttpStatus.UNAUTHORIZED.value())
      }
    }
  }

  def getUserIdFromHttpServletRequest(request: HttpServletRequest): Option[String] = {
    Option(request.getHeader("x-ms-client-principal-name")).orElse(
      Option(request.getCookies).map(
        cookies => cookies.find(_.getName == "AppServiceAuthSession").map(_.getValue)
          .map(appServiceAuthSession => userService.getUserId(appServiceAuthSession))
          .orNull
      )
    )
  }

}
