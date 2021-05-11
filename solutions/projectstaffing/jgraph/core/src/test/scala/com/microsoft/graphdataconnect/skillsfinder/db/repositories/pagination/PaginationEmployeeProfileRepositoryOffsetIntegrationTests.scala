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

class PaginationEmployeeProfileRepositoryOffsetIntegrationTests extends AbstractIntegrationTestBase {

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _
  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _


  /*
   * Offset
   * with Recommended employees
   */

  @Test
  def pagination_RecommendedOffset_OneOfTwo_one_1(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 1, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin1@fake.com', 'admin2@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)
  }

  @Test
  def pagination_RecommendedOffset_OneOfTwo_one_2(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 1, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_RecommendedOffset_MoreOfMore_one_1(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var hrDataProfile3: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile3.composedId = HRDataEmployeeIdentity("admin3@fake.com", version)
    hrDataProfile3 = hrDataEmployeeProfileRepository.save(hrDataProfile3)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    var employeeProfile3: EmployeeProfile = new EmployeeProfile()
    employeeProfile3.composedId = EmployeeIdentity("3", version)
    employeeProfile3.mail = "admin3@fake.com"
    employeeProfile3 = employeeProfileRepository.save(employeeProfile3)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 1, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin1@fake.com', 'admin2@fake.com', 'admin3@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile3.composedId.id)
  }

  @Test
  def pagination_RecommendedOffset_MoreOfMore_one_2(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var hrDataProfile3: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile3.composedId = HRDataEmployeeIdentity("admin3@fake.com", version)
    hrDataProfile3 = hrDataEmployeeProfileRepository.save(hrDataProfile3)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    var employeeProfile3: EmployeeProfile = new EmployeeProfile()
    employeeProfile3.composedId = EmployeeIdentity("3", version)
    employeeProfile3.mail = "admin3@fake.com"
    employeeProfile3 = employeeProfileRepository.save(employeeProfile3)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 1, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin3@fake.com', 'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_RecommendedOffset_MoreOfTwo_more(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 6, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

  /*
  * Offset
  * without any Recommendation
  */

  @Test
  def pagination_Offset_OneOfTwo_one_1(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 1, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Offset_OneOfTwo_one_2(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 1, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Offset_MoreOfMore_one_1(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var hrDataProfile3: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile3.composedId = HRDataEmployeeIdentity("admin3@fake.com", version)
    hrDataProfile3 = hrDataEmployeeProfileRepository.save(hrDataProfile3)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    var employeeProfile3: EmployeeProfile = new EmployeeProfile()
    employeeProfile3.composedId = EmployeeIdentity("3", version)
    employeeProfile3.mail = "admin3@fake.com"
    employeeProfile3 = employeeProfileRepository.save(employeeProfile3)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 1, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Offset_MoreOfMore_one_2(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var hrDataProfile3: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile3.composedId = HRDataEmployeeIdentity("admin3@fake.com", version)
    hrDataProfile3 = hrDataEmployeeProfileRepository.save(hrDataProfile3)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "admin2@fake.com"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    var employeeProfile3: EmployeeProfile = new EmployeeProfile()
    employeeProfile3.composedId = EmployeeIdentity("3", version)
    employeeProfile3.mail = "admin3@fake.com"
    employeeProfile3 = employeeProfileRepository.save(employeeProfile3)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 1, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Offset_MoreOfTwo_more(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 6, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

}
