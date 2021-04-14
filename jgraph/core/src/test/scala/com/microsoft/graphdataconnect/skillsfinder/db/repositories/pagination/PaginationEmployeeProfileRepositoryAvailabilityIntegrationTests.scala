package com.microsoft.graphdataconnect.skillsfinder.db.repositories.pagination

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

class PaginationEmployeeProfileRepositoryAvailabilityIntegrationTests extends AbstractIntegrationTestBase {

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _
  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _

  /*
   * Availability
   * with Recommended employees
   */

  @Test
  def pagination_Availability_Recommended_null(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Availability_Recommended_OneValue_In(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2021-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Availability_Recommended_OneValue_Out(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2030-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)
  }

  @Test
  def pagination_Availability_Recommended_TwoValues_InAndOut(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2030-10-10"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2.availableStartingFrom = "2020-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)
  }

  @Test
  def pagination_Availability_Recommended_TwoValues_Out(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2030-10-10"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2.availableStartingFrom = "2031-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

  @Test
  def pagination_Availability_Recommended_SameDate(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2020-10-10"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2.availableStartingFrom = "2022-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  /*
   * Availability
   * without any Recommendation
   */

  @Test
  def pagination_Availability_null(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Availability_OneValue_In(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2021-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }

  @Test
  def pagination_Availability_OneValue_Out(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2030-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)
  }

  @Test
  def pagination_Availability_TwoValues_InAndOut(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2030-10-10"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2.availableStartingFrom = "2020-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)
  }

  @Test
  def pagination_Availability_TwoValues_Out(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2030-10-10"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2.availableStartingFrom = "2031-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

  @Test
  def pagination_Availability_ExactMatchDate(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1.availableStartingFrom = "2020-10-10"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("admin2@fake.com", version)
    hrDataProfile2.availableStartingFrom = "2022-10-10"
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
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile1.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile2.composedId.id)
  }

  @Test
  def pagination_Availability_TwoSameDate_1(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("A", version)
    hrDataProfile1.availableStartingFrom = "2020-10-10"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("B", version)
    hrDataProfile2.availableStartingFrom = "2020-10-10"
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "A"
    employeeProfile1.displayName = "A"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "B"
    employeeProfile2.displayName = "B"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile1.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile2.composedId.id)
  }

  @Test
  def pagination_Availability_TwoSameDate_2(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("B", version)
    hrDataProfile1.availableStartingFrom = "2020-10-10"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("A", version)
    hrDataProfile2.availableStartingFrom = "2020-10-10"
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "B"
    employeeProfile1.displayName = "B"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("2", version)
    employeeProfile2.mail = "A"
    employeeProfile2.displayName = "A"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 2)

    // Extra Assert - Preserve the order
    val rs1 = result.get(0)
    Assert.assertTrue("Employee profile incorrect id", rs1.employee_id == employeeProfile2.composedId.id)

    val rs2 = result.get(1)
    Assert.assertTrue("Employee profile incorrect id", rs2.employee_id == employeeProfile1.composedId.id)
  }


}
