/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.config

@Configuration
class UserIdExtractionFilter(@Autowired val userService: UserService) extends HttpFilter {
  private val log: Logger = LoggerFactory.getLogger(classOf[UserIdExtractionFilter])
  private val arrayAnonymousUserInfo = Map("accesToken" -> "", "idToken" -> "", "refreshToken" -> "", "userId" -> "test@anonymous.com")

  @Value("${anonymous.user.default.email}")
  private var anonymousUserDefaultEmail: String = _

  @Value("${anonymous.authentication.enabled}")
  var isAnonymousAuthEnabled: Boolean  = _

  @throws[IOException]
  @throws[ServletException]
  override protected def doFilter(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain): Unit = {
    try {

      if (isAnonymousAuthEnabled) {
        request.setAttribute("userId", anonymousUserDefaultEmail)
        filterChain.doFilter(request, response)
      } else {
        val userId: Option[String] = getUserIdFromHttpServletRequest(request)

        if (userId.isDefined) {
          request.setAttribute("userId", userId.get)
        } else {
          throw new UnauthorizedException("Authentication failed!")
        }
        filterChain.doFilter(request, response)
      }
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
