/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers

import com.microsoft.graphdataconnect.watercooler.common.exceptions.NotFoundException
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.employee.EmployeeProfileResponse
import com.microsoft.graphdataconnect.watercooler.core.managers.EmployeeManager
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestMapping, RequestParam, RestController}

@RestController
@RequestMapping(Array("/jwc/employee"))
class EmployeeController(@Autowired employeeManager: EmployeeManager) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[EmployeeController])

  @GetMapping()
  def getEmployeeByEmail(@RequestParam(name = "email", required = true) mail: String): ResponseEntity[EmployeeProfileResponse] = {

    logger.info(s"Asked for employee profile with mail: $mail")
    val employee: Option[EmployeeProfileResponse] = employeeManager.findEmployeeByEmail(mail)
    if (employee.isDefined)
      ResponseEntity.status(HttpStatus.OK).body(employee.get)
     else
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(throw new NotFoundException("Cannot find employee with mail: " + mail))
  }

  @GetMapping(Array("/{id}"))
  def getEmployeeById(@PathVariable(name = "id", required = true) id: String): ResponseEntity[EmployeeProfileResponse] = {

    logger.info(s"Asked for employee profile with id: $id")
    val employee: Option[EmployeeProfileResponse] = employeeManager.findEmployeeById(id)
    if (employee.isDefined)
      ResponseEntity.status(HttpStatus.OK).body(employee.get)
    else
      ResponseEntity.status(HttpStatus.NOT_FOUND).body(throw new NotFoundException("Cannot find employee with id: " + id))
  }

}
