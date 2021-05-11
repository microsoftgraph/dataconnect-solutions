/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.pagination

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessResourceUsageException

class PaginationEmployeeProfileRepositorySizeIntegrationTests extends AbstractIntegrationTestBase {

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _
  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _

  /*
   * Size
   * with recommended employee
   */
  @Test
  def pagination_Recommended_Size_one(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    // Act
    // ask for 1 records (recommended)
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)
  }

  @Test
  def pagination_Recommended_Size_two(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    // Act
    // ask for 2 records (recommended)
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin1@fake.com', 'admin2@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)
  }


  @Test
  def pagination_Recommended_Size_OneOfTwo(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    // Act
    // ask for 1 of two record (recommended)
    val result = employeeProfileRepository.findRecommendedEmployees(1, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)
  }

  @Test
  def pagination_Recommended_Size_MoreOfTwo(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(5, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  /*
   * Size
   * without any recommendation
   */

  @Test
  def pagination_Size_null(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"

    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    // Act
    // ask for null records (recommended)
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

  @Test
  def pagination_Size_empty(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    // ask for 0 records (recommended)
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

  @Test
  def pagination_Size_MoreOfTwo(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(5, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0) // null name
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1) // null name
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Size_OneOfTwo(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(1, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0) // null name
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)
  }

  @Test(expected = classOf[InvalidDataAccessResourceUsageException])
  def pagination_Size_Zero(): Unit = {
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    employeeProfileRepository.findRecommendedEmployees(0, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")
  }

}
