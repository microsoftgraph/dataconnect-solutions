/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.managers

import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.EmployeeProfile
import com.microsoft.graphdataconnect.watercooler.common.services.employee.EmployeeProfileService
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.employee.EmployeeProfileResponse
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EmployeeManager(@Autowired employeeProfileService: EmployeeProfileService) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[EmployeeManager])

  def findEmployeeByEmail(mail: String): Option[EmployeeProfileResponse] = {
    val employeeProfileOpt: Option[EmployeeProfile] = employeeProfileService.findEmployeeByEmail(mail)

    employeeProfileOpt.map { employeeProfile =>
        EmployeeProfileResponse(
        id = employeeProfile.composedId.id,
        mail = employeeProfile.mail,
        displayName = employeeProfile.displayName,
        aboutMe = employeeProfile.aboutMe,
        jobTitle = employeeProfile.jobTitle,
        companyName = employeeProfile.companyName,
        department = employeeProfile.department,
        city = employeeProfile.city,
        state = employeeProfile.state,
        country = employeeProfile.country,
        skills = employeeProfile.skills,
        responsibilities = employeeProfile.responsibilities,
        engagement = employeeProfile.engagement,
        image = employeeProfile.image
      )
    }

  }

  def findEmployeeById(id: String): Option[EmployeeProfileResponse] = {
    val employeeProfileOpt: Option[EmployeeProfile] = employeeProfileService.findEmployeeById(id)

    employeeProfileOpt.map { employeeProfile =>
      EmployeeProfileResponse(
        id = employeeProfile.composedId.id,
        mail = employeeProfile.mail,
        displayName = employeeProfile.displayName,
        aboutMe = employeeProfile.aboutMe,
        jobTitle = employeeProfile.jobTitle,
        companyName = employeeProfile.companyName,
        department = employeeProfile.department,
        city = employeeProfile.city,
        state = employeeProfile.state,
        country = employeeProfile.country,
        skills = employeeProfile.skills,
        responsibilities = employeeProfile.responsibilities,
        engagement = employeeProfile.engagement,
        image = employeeProfile.image
      )
    }

  }

}
