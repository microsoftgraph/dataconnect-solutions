/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.controllers

import java.time.{LocalDateTime, ZoneOffset}

import com.microsoft.graphdataconnect.skillsfinder.db.entities.Configuration
import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.ConfigurationRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.Employee
import com.microsoft.graphdataconnect.skillsfinder.models.configs
import com.microsoft.graphdataconnect.skillsfinder.models.configs.EmployeeProfileVersionConfiguration
import com.microsoft.graphdataconnect.skillsfinder.service.ConfigurationService
import com.microsoft.graphdataconnect.skillsfinder.setup.web.AbstractWebIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class EmployeeControllerWebIntegrationTests extends AbstractWebIntegrationTestBase {

  private val log: Logger = LoggerFactory.getLogger(classOf[EmployeeControllerWebIntegrationTests])

  @Autowired
  var gdcRestClient: GDCRestClient = _
  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _
  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _
  @Autowired
  var configurationRepository: ConfigurationRepository = _
  @Autowired
  var configurationService: ConfigurationService = _

  private val employeeId = "1"
  private val v0 = TimeUtils.oldestSqlDate
  private val v1 = LocalDateTime.now(ZoneOffset.UTC)
  private var epV0: EmployeeProfile = new EmployeeProfile()
  private var epV1: EmployeeProfile = new EmployeeProfile()

  private var hrepV0 = new HRDataEmployeeProfile()
  private var hrepV1 = new HRDataEmployeeProfile()

  override def beforeClassSetup(): Unit = {
    // User V0
    epV0.composedId = EmployeeIdentity(employeeId, v0)
    epV0.mail = "admin1@fake.com"
    epV0.displayName = "Admin 1"
    epV0 = employeeProfileRepository.save(epV0)

    hrepV0.composedId = new HRDataEmployeeIdentity("admin1@fake.com", v0)
    hrepV0.name = "Admin 1"
    hrepV0 = hrDataEmployeeProfileRepository.save(hrepV0)

    // User V1
    epV1.composedId = EmployeeIdentity(employeeId, v1)
    epV1.mail = "admin1@fake.com"
    epV1.displayName = "Admin 1"
    epV1 = employeeProfileRepository.save(epV1)

    hrepV1.composedId = new HRDataEmployeeIdentity("admin1@fake.com", v1)
    hrepV1.name = "Admin 1"
    hrepV1 = hrDataEmployeeProfileRepository.save(hrepV1)

    // Set default version
    configurationRepository.save(Configuration(EmployeeProfileVersionConfiguration(v0)))
    configurationService.getLatestEmployeeProfileVersionFromCache(true)
  }

  @Test
  @Transactional
  def versioningWorksOnEmployeeProfile(): Unit = {

    // Act
    val employee0: Employee = gdcRestClient.getEmployee(employeeId)

    // Assert that first version is returned
    Assert.assertTrue(employee0.id == employeeId)
    Assert.assertTrue(employee0.mail == epV0.mail)
    Assert.assertTrue(employee0.name == epV0.displayName)

    // Update the latest version
    configurationRepository.save(Configuration(configs.EmployeeProfileVersionConfiguration(v1)))
    // refresh cache
    val newVersion: LocalDateTime = configurationService.getLatestEmployeeProfileVersionFromCache(true)
    Assert.assertTrue(newVersion == v1)

    // Act
    val employee1: Employee = gdcRestClient.getEmployee(employeeId)

    // Assert that 2nd version is returned
    Assert.assertTrue(employee1.id == employeeId)
    Assert.assertTrue(employee1.mail == epV1.mail)
    Assert.assertTrue(employee1.name == epV1.displayName)
  }

  override def afterClassSetup(): Unit = {
    employeeProfileRepository.delete(epV0)
    employeeProfileRepository.delete(epV1)

    hrDataEmployeeProfileRepository.delete(hrepV0)
    hrDataEmployeeProfileRepository.delete(hrepV1)

    // Set default version
    configurationRepository.save(Configuration(configs.EmployeeProfileVersionConfiguration(v0)))
    configurationService.getLatestEmployeeProfileVersionFromCache(true)
  }

}
