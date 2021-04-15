package com.microsoft.graphdataconnect.skillsfinder.controllers.admin

import com.microsoft.graphdataconnect.skillsfinder.models.response.AdminCheckResponse
import com.microsoft.graphdataconnect.skillsfinder.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestAttribute, RequestHeader, RequestMapping}

@Controller
@RequestMapping(Array("/gdc/admin/user"))
class AdminUserController(@Autowired val userService: UserService) {

  @GetMapping()
  def isUserAdmin(@RequestHeader httpHeaders: HttpHeaders, @RequestAttribute("userId") userId: String): ResponseEntity[AdminCheckResponse] = {
    if (userService.isCurrentUserAnAdmin(httpHeaders)) {
      ResponseEntity.status(HttpStatus.OK).body(AdminCheckResponse(true))
    } else {
      ResponseEntity.status(HttpStatus.OK).body(AdminCheckResponse(false))
    }
  }

}
