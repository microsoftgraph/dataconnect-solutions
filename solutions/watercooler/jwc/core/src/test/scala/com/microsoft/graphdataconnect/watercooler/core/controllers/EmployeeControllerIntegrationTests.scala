/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers

import java.time.{LocalDateTime, ZoneOffset}

import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.EmployeeIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.entities.meta.ConfigurationTypes
import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.watercooler.common.db.entities.meta.{Configuration, ConfigurationTypes}
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.meta.ConfigurationRepository
import com.microsoft.graphdataconnect.watercooler.common.services.meta.ConfigurationService
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.employee.EmployeeProfileResponse
import com.microsoft.graphdataconnect.watercooler.core.setup.web.AbstractWebIntegrationTestBase
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class EmployeeControllerIntegrationTests extends AbstractWebIntegrationTestBase {

  @Autowired
  var jwcRestClient: JWCRestClient = _
  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _

  @Autowired
  var configurationRepository: ConfigurationRepository = _
  @Autowired
  var configurationService: ConfigurationService = _

  private val employeeId = "1"
  private val v0 = TimeUtils.oldestSqlDate
  private val v1 = LocalDateTime.now(ZoneOffset.UTC)
  private var epV0: EmployeeProfile = new EmployeeProfile()
  private var epV1: EmployeeProfile = new EmployeeProfile()


  override def beforeClassSetup(): Unit = {
    // User V0
    epV0.composedId = EmployeeIdentity(employeeId, v0)
    epV0.mail = "admin1@fake.com"
    epV0.displayName = "Admin 1"
    epV0 = employeeProfileRepository.save(epV0)

    // User V1
    epV1.composedId = EmployeeIdentity(employeeId, v1)
    epV1.mail = "admin1@fake.com"
    epV1.displayName = "Admin 1"
    epV1 = employeeProfileRepository.save(epV1)

    // Set default version
    configurationRepository.save(Configuration(ConfigurationTypes.LatestVersionOfEmployeeProfiles,  TimeUtils.localDateTimeToString(v0)) )
    configurationService.getLatestEmployeeProfileDataVersionFromCache(true)
  }

  @Test
  @Transactional
  def versioningWorksOnEmployeeProfile(): Unit = {

    // Act
    val employee0: EmployeeProfileResponse = jwcRestClient.getEmployeeById(employeeId)

    // Assert that first version is returned
    Assert.assertTrue(employee0.id == employeeId)
    Assert.assertTrue(employee0.mail == epV0.mail)
    Assert.assertTrue(employee0.displayName == epV0.displayName)

    // Update the latest version
    configurationRepository.save(Configuration(ConfigurationTypes.LatestVersionOfEmployeeProfiles,  TimeUtils.localDateTimeToString(v1)) )
    // refresh cache
    val newVersion: LocalDateTime = configurationService.getLatestEmployeeProfileDataVersionFromCache(true)
    Assert.assertTrue(newVersion == v1)

    // Act
    val employee1: EmployeeProfileResponse = jwcRestClient.getEmployeeById(employeeId)

    // Assert that 2nd version is returned
    Assert.assertTrue(employee1.id == employeeId)
    Assert.assertTrue(employee1.mail == epV1.mail)
    Assert.assertTrue(employee1.displayName == epV1.displayName)
  }

  override def afterClassSetup(): Unit = {
    employeeProfileRepository.delete(epV0)
    employeeProfileRepository.delete(epV1)

    // Set default version
    configurationRepository.save(Configuration(ConfigurationTypes.LatestVersionOfEmployeeProfiles,  TimeUtils.localDateTimeToString(v0)))
    configurationService.getLatestEmployeeProfileDataVersionFromCache(true)
  }

}
