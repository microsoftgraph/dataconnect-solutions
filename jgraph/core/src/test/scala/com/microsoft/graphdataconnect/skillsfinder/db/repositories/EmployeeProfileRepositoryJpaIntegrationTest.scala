package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import java.util
import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeProfile, RecommendedEmployee}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.EmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

class EmployeeProfileRepositoryJpaIntegrationTest extends AbstractIntegrationTestBase {

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _
  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _

  @Test
  def findByEmailReturnsCorrectEmail(): Unit = {
    val version = TimeUtils.oldestSqlDate
    val employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "employee1@company.com"

    val employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("321", version)
    employeeProfile2.mail = "employee2@company.com"

    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    val foundEmployeeProfile: Optional[EmployeeProfile] = employeeProfileRepository.findByComposedId(employeeProfile1.composedId)

    Assert.assertTrue("Employee profile not found", foundEmployeeProfile.isPresent)

    Assert.assertTrue("Found incorrect employee profile",
      employeeProfile1.composedId.id.equals(foundEmployeeProfile.get().composedId.id) && employeeProfile1.mail.equals(foundEmployeeProfile.get().mail))
  }

  @Test
  def findByIdReturnsCorrectEmail(): Unit = {
    val version = TimeUtils.oldestSqlDate
    val employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "employee1@company.com"

    val employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("321", version)
    employeeProfile2.mail = "employee2@company.com"

    employeeProfileRepository.save(employeeProfile1)
    employeeProfileRepository.save(employeeProfile2)

    val foundEmployeeProfile: Optional[EmployeeProfile] = employeeProfileRepository.findById(employeeProfile2.composedId)

    Assert.assertTrue("Employee profile not found", foundEmployeeProfile.isPresent)

    Assert.assertTrue("Found incorrect employee profile",
      employeeProfile2.composedId.id.equals(foundEmployeeProfile.get().composedId.id) && employeeProfile2.mail.equals(foundEmployeeProfile.get().mail))
  }

  @Test
  def findRecommendedEmployees_Works(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)
  }

  @Test
  def findRecommendedEmployees_HR_mandatory_withData(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = true, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)
  }

  @Test
  def findRecommendedEmployees_HR_mandatory_withoutData(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"

    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(1, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = true, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 0)
  }

  @Test
  def findRecommendedEmployees_HR_notMandatory_withData(): Unit = {
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
    val result = employeeProfileRepository.findRecommendedEmployees(1, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)
  }

  @Test
  def findRecommendedEmployees_HR_notMandatory_withoutData(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "admin1@fake.com"

    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    // Act
    val result = employeeProfileRepository.findRecommendedEmployees(1, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, "")

    // Assert
    Assert.assertTrue("Employee profile not found", result.size() == 1)
  }

  @Test
  def findRecommendedEmployees_filterForM365City(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.city = "Austin"
    employeeProfile1.mail = "john@company.com"


    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("124", version)
    employeeProfile2.city = "New York"
    employeeProfile2.mail = "daniel@company.com"

    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)
    val city = "Austin"
    // Act
    val jsonFilter = s"""[{"dbTable": "employee_profile","dbColumn": "city", "filterValues": ["$city"]}]"""
    val result: util.List[RecommendedEmployee] = employeeProfileRepository.findRecommendedEmployees(2, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, jsonFilter)

    // Assert
    Assert.assertTrue(f"One of the employees should be in search result because only one is from $city", result.size() == 1)

    val employeeLocation = result.get(0).location

    Assert.assertTrue(f"The location of the retrieved employee should contain the city $city", employeeLocation.contains(city))
  }


  @Test
  def findRecommendedEmployees_filterForM365Department_AndHRDataLocation(): Unit = {
    // Arrange
    val version = TimeUtils.oldestSqlDate

    var employeeProfile1: EmployeeProfile = new EmployeeProfile()
    employeeProfile1.composedId = EmployeeIdentity("123", version)
    employeeProfile1.mail = "john@company.com"
    employeeProfile1.department = "Engineering"
    employeeProfile1 = employeeProfileRepository.save(employeeProfile1)

    var hrDataProfile1: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile1.composedId = HRDataEmployeeIdentity("john@company.com", version)
    hrDataProfile1.location = "Romania, Timisoara"
    hrDataProfile1 = hrDataEmployeeProfileRepository.save(hrDataProfile1)

    var employeeProfile2: EmployeeProfile = new EmployeeProfile()
    employeeProfile2.composedId = EmployeeIdentity("124", version)
    employeeProfile2.mail = "daniel@company.com"
    employeeProfile2.department = "Marketing"
    employeeProfile2 = employeeProfileRepository.save(employeeProfile2)

    var hrDataProfile2: HRDataEmployeeProfile = new HRDataEmployeeProfile()
    hrDataProfile2.composedId = HRDataEmployeeIdentity("daniel@company.com", version)
    hrDataProfile2.location = "USA, TX, Austin"
    hrDataProfile2 = hrDataEmployeeProfileRepository.save(hrDataProfile2)

    val department = "Engineering"
    val location = "Romania, Timisoara"
    // Act
    val jsonFilter =
      s"""[{"dbTable": "employee_profile","dbColumn": "department", "filterValues": ["$department"]},{"dbTable": "hr_data_employee_profile","dbColumn": "location", "filterValues": ["$location"]}]""".stripMargin
    val result: util.List[RecommendedEmployee] = employeeProfileRepository.findRecommendedEmployees(2, 0, orderByAvailabilityAndName = true, "2022-10-10", "admin@fake.com", null, null, null, null, isHRDataMandatory = false, jsonFilter)

    // Assert
    Assert.assertTrue(f"One of the employees should be in search result because only one is from $location and works in  $department", result.size() == 1)

    val employeeLocation = result.get(0).location
    val employeeDepartment = result.get(0).department

    Assert.assertTrue(f"The location of the retrieved employee should be $location", employeeLocation.equals(location))
    Assert.assertTrue(f"The department of the retrieved employee should be $department", employeeDepartment.equals(department))
  }

}
