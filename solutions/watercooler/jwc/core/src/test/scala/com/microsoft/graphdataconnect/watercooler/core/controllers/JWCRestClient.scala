/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers

import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.employee.EmployeeProfileResponse
import com.microsoft.graphdataconnect.watercooler.core.setup.Constants
import com.microsoft.graphdataconnect.watercooler.core.setup.web.RestHelper
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.{HttpEntity, HttpMethod, ResponseEntity}
import org.springframework.stereotype.Component

@Component
class JWCRestClient extends RestHelper {

  def getEmployeeById(id: String): EmployeeProfileResponse = {
    val request = new HttpEntity[String](getBaseHeaders)
    val url: String = getRelativeResourceURI(Constants.EMPLOYEE_RESOURCE + "/" + id)

    val response: ResponseEntity[EmployeeProfileResponse] = restClient.exchange(
      url,
      HttpMethod.GET,
      request,
      classOf[EmployeeProfileResponse])

    assertThat(response.getStatusCode.is2xxSuccessful())
    response.getBody
  }

  def getEmployeeByMail(mail: String): EmployeeProfileResponse = {
    val request = new HttpEntity[String](getBaseHeaders)
    val url: String = getRelativeResourceURI(Constants.EMPLOYEE_RESOURCE + "?email=" + mail)

    val response: ResponseEntity[EmployeeProfileResponse] = restClient.exchange(
      url,
      HttpMethod.GET,
      request,
      classOf[EmployeeProfileResponse])

    assertThat(response.getStatusCode.is2xxSuccessful())
    response.getBody
  }

}
