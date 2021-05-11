/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

class HRDataEmployeeProfileRepositoryTest extends AbstractIntegrationTestBase {

  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _

  @Test
  def findByEmailReturnsCorrectEmail(): Unit = {

    val employeeProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    val version = TimeUtils.oldestSqlDate
    employeeProfile1.composedId = new HRDataEmployeeIdentity("employee1@company.com", version)

    val savedEmployee = hrDataEmployeeProfileRepository.save(employeeProfile1)

    val foundEmployeeProfile: Optional[HRDataEmployeeProfile] = hrDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(employeeProfile1.composedId.mail, version)

    Assert.assertTrue("HRData employee profile not found", foundEmployeeProfile.isPresent)

    Assert.assertTrue("Found incorrect HRData employee profile",
      savedEmployee.composedId.mail.equals(foundEmployeeProfile.get().composedId.mail))
  }

  @Test
  def findByIDReturnsCorrectEmail(): Unit = {

    val employeeProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    val version = TimeUtils.oldestSqlDate
    employeeProfile1.composedId = new HRDataEmployeeIdentity("employee1@company.com", version)

    val savedEmployee = hrDataEmployeeProfileRepository.save(employeeProfile1)

    val foundEmployeeProfile: Optional[HRDataEmployeeProfile] = hrDataEmployeeProfileRepository.findById(new HRDataEmployeeIdentity("employee1@company.com", version))

    Assert.assertTrue("HRData employee profile should have been found", foundEmployeeProfile.isPresent)

    Assert.assertTrue("Found incorrect HRData employee profile",
      savedEmployee.composedId.mail.equals(foundEmployeeProfile.get().composedId.mail))
  }

}
