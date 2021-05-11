/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.skillsfinder.exceptions.ResourceNotFoundException
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.Employee
import com.microsoft.graphdataconnect.skillsfinder.service.EmployeeSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestAttribute, RequestMapping}

@Controller
@RequestMapping(Array("/gdc/employee"))
class EmployeeController(@Autowired employeeSearchService: EmployeeSearchService) {

  @GetMapping(Array("/{id}"))
  def getEmployee(@PathVariable("id") id: String,
                  @RequestAttribute("userId") userId: String): ResponseEntity[Employee] = {
    employeeSearchService.getEmployee(id, userId) match {
      case Some(employee) => ResponseEntity.status(HttpStatus.OK).body(employee)
      case None => throw new ResourceNotFoundException(s"Employee: $id not found")
    }
  }

}
