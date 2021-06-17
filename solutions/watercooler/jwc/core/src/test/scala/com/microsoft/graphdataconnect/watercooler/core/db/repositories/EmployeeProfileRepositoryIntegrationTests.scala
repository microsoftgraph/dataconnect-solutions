/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.db.repositories

import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.EmployeeIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.core.setup.repository.AbstractIntegrationTestBase
import org.junit.{Assert, Test}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired

class EmployeeProfileRepositoryIntegrationTests extends AbstractIntegrationTestBase {

  private val logger: Logger = LoggerFactory.getLogger(classOf[EmployeeProfileRepositoryIntegrationTests])

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _

  @Test
  def testFindByMailAndComposedIdVersion(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "employee1@company.com"

    employeeProfileRepository.save(employeeProfile1)

    // act
    val employee: Option[EmployeeProfile] =  Option(employeeProfileRepository.findByMailAndComposedIdVersion(employeeProfile1.mail, version).orElse(null))

    // assert
    Assert.assertTrue("employee should not be empty", employee.nonEmpty) // TODO - there is no data yet
    Assert.assertEquals(employee.get.mail, employeeProfile1.mail) // TODO - there is no data yet
    Assert.assertEquals(employee.get.composedId, employeeProfile1.composedId) // TODO - there is no data yet
  }

}
