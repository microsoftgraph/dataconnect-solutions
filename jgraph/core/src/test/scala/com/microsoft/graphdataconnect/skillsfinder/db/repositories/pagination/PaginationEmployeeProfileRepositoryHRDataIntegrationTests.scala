package com.microsoft.graphdataconnect.skillsfinder.db.repositories.pagination

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

class PaginationEmployeeProfileRepositoryHRDataIntegrationTests extends AbstractIntegrationTestBase {

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _
  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _

  /*
   * HR Data
   * with Recommended employees
   */

  @Test
  def pagination_NoHRData_Recommended_one(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Expected Employee profile even though HR Data is missing", result.size() == 1)
  }

  @Test
  def pagination_OnlyHRData_Recommended_zero(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = false, "2022-10-10", "admin@fake.com", "'admin2@fake.com', 'admin1@fake.com'", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

  /*
   * HR Data
   * without any Recommendation
   */

  @Test
  def pagination_NoHRData_one(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Expected one Employee profile even though HR Data is missing", result.size() == 1)
  }

  @Test
  def pagination_OnlyHRData_zero(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate
    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("admin1@fake.com", version)
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(10, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", "''", null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

}
