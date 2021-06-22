/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.services.employee

import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.EmployeeIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EmployeeProfileService(@Autowired configurationService: ConfigurationService,
                             @Autowired employeeProfileRepository: EmployeeProfileRepository) {

  def findEmployeeByEmail(mail: String): Option[EmployeeProfile] = {
    val latestVersion = configurationService.getLatestEmployeeProfileDataVersionFromCache()
    Option(employeeProfileRepository.findByMailAndComposedIdVersion(mail, latestVersion).orElse(null))
  }

  def findEmployeeById(id: String): Option[EmployeeProfile] = {
    val latestVersion = configurationService.getLatestEmployeeProfileDataVersionFromCache()
    Option(employeeProfileRepository.findById(EmployeeIdentity(id, latestVersion)).orElse(null))
  }

}
