/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeInferredRoles, EmployeeInferredRolesIdentity}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeInferredRolesRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

class EmployeeInferredRolesRepositoryTest extends AbstractIntegrationTestBase {

  @Autowired
  var employeeInferredRolesRepository: EmployeeInferredRolesRepository = _

  @Test
  def findByEmailReturnsCorrectInferredRoles(): Unit = {
    val employeeInferredRoles = new EmployeeInferredRoles()
    val version = TimeUtils.oldestSqlDate
    employeeInferredRoles.composedId = new EmployeeInferredRolesIdentity(id = "random_id", version = version)
    employeeInferredRoles.totalDocs = 0
    employeeInferredRoles.email = "some_user@example.com"

    employeeInferredRolesRepository.save(employeeInferredRoles)
    val foundEmployeeInferredRols = employeeInferredRolesRepository.findByEmailAndComposedIdVersion(email = employeeInferredRoles.email, version = version)

    Assert.assertTrue(s"There should be in the inferred roles table a row linked to an employee with the email address $employeeInferredRoles.email", foundEmployeeInferredRols.isPresent)

    Assert.assertTrue(s"The found employee inferred roles record should have the composeId=${employeeInferredRoles.composedId} and email=${employeeInferredRoles.email}", foundEmployeeInferredRols.get().composedId.id.equals(employeeInferredRoles.composedId.id) &&
      foundEmployeeInferredRols.get().email.equals(employeeInferredRoles.email))
  }


}
