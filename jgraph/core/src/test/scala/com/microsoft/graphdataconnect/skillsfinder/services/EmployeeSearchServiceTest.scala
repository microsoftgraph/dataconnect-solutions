package com.microsoft.graphdataconnect.skillsfinder.services

import java.time.LocalDateTime
import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeInferredRoles, EmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.{EmployeeInferredRolesRepository, EmployeeProfileRepository}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.Employee
import com.microsoft.graphdataconnect.skillsfinder.service.{ConfigurationService, EmployeeSearchService}
import com.microsoft.graphdataconnect.skillsfinder.services.EmployeeSearchServiceTest._
import org.junit.{Assert, BeforeClass, Test}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}

import scala.compat.java8.OptionConverters._

class EmployeeSearchServiceTest {


  @Test
  def testEmployeeSorting(): Unit = {
    val employees = List(
      Employee("1", "Maya Silverstone", "msilverstone@example.com", "Junior Data Scientist", List.empty, "Cupertino, CA, US",
        "Junior Data Scientist passionate about data",
        List("Mathematics", "Statistics", "Data Analysis", "Data Visualisation", "Machine Learning", "Deep Learning", "Python", "R", "Keras", "Tensor Flow", "Torch"),
        null,
        List("Java", "Statistics", "Python", "Keras", "Scala", "Torch", "PowerBI"),
        Map("Domain1" -> List("term1", "term2"), "Domain2" -> List("term3", "term4")),
        null,
        List("Distributed computing"),
        "Nash",
        "R&D",
        "Jeremy Wilson",
        "jwilson@example.com",
        "2020-09-10",
        "https://www.linkedin.com/in/test_profile/",
        None,
        null),
      Employee("2", "Alicia Smith", "asmith@example.com", "Junior Data Scientist", List.empty, "Seattle, WA, US",
        "Just starting my journey as a Data Scientist",
        List("Python", "Statistics", "Tensor Flow"),
        null,
        List("Statistics", "Python", "R"),
        Map("Domain1" -> List("term1", "term2"), "Domain2" -> List("term3", "term4")),
        null,
        List("Distributed computing"),
        "GDC",
        null,
        "Jeremy Wilson",
        "jwilson@example.com",
        "2020-08-31",
        "https://www.linkedin.com/in/test_profile/",
        None,
        null),
      Employee("3", "Jane Doe", "janedoe@example.com", null, null, null,
        null,
        List("Python", "Machine Learning", "R"),
        null,
        List("Statistics", "Python", "R"),
        Map("Domain1" -> List("term1", "term2"), "Domain2" -> List("term3", "term4")),
        null,
        List("Distributed computing"),
        null,
        null,
        null,
        null,
        null,
        null,
        None,
        null),
      Employee("4", "Brad Preston", "bpreston@example.com", "Senior Data Scientist", List.empty, "Warsaw, Poland",
        "Senior Data Scientist looking for hidden gems in data",
        List("Python", "Machine Learning", "R", "Statistics"),
        null,
        List("Statistics", "Python", "R"),
        Map("Domain1" -> List("term1", "term2"), "Domain2" -> List("term3", "term4")),
        null,
        List("Distributed computing"),
        "GDC",
        "R&D",
        "Jeremy Wilson",
        "jwilson@example.com",
        "2020-09-10",
        "https://www.linkedin.com/in/test_profile/",
        None,
        null),
      Employee("5", "John Door", "jdoor@example.com", null, null,
        null, null,
        List("Python", "Machine Learning", "R"),
        null,
        List("Statistics", "Python", "R"),
        Map("Domain1" -> List("term1", "term2"), "Domain2" -> List("term3", "term4")),
        null,
        List("Distributed computing"),
        null,
        null,
        null,
        null,
        null,
        "https://www.linkedin.com/in/test_profile/",
        None,
        null),
      Employee("6", "John Doe", "jdoe@example.com", null, null,
        null, null,
        List("Python", "Machine Learning", "R"),
        null,
        List("Statistics", "Python", "R"),
        Map("Domain1" -> List("term1", "term2"), "Domain2" -> List("term3", "term4")),
        null,
        List("Distributed computing"),
        null,
        null,
        null,
        null,
        null,
        null,
        None,
        null))

    val sortedEmployees = EmployeeSearchService.sortEmployeesByAvailabilityAndName(employees)
    println(sortedEmployees.map(employee => (employee.name, employee.availableSince, employee.id)))
    Assert.assertArrayEquals(Array(3, 6, 5, 2, 4, 1), sortedEmployees.map(_.id.toInt).toArray)
  }

  @Test
  def testComputeM365EmployeeLocation(): Unit = {
    Assert.assertEquals("Timisoara, Timis, Romania", EmployeeSearchService.computeM365EmployeeLocation(city = "Timisoara", state = "Timis", country = "Romania"))
    Assert.assertEquals("", EmployeeSearchService.computeM365EmployeeLocation(city = null, state = null, country = null))
    Assert.assertEquals("", EmployeeSearchService.computeM365EmployeeLocation(city = "", state = "", country = ""))
    Assert.assertEquals("Romania", EmployeeSearchService.computeM365EmployeeLocation(city = "", state = null, country = "Romania"))
    Assert.assertEquals("Timisoara, Timis", EmployeeSearchService.computeM365EmployeeLocation(city = "Timisoara", state = "Timis", country = null))
    Assert.assertEquals("Timisoara, Romania", EmployeeSearchService.computeM365EmployeeLocation(city = null, state = "Timisoara", country = "Romania"))
  }

  @Test
  def testGetFullM365EmployeeWithoutHRData(): Unit = {
    // Arrange
    val authenticated_user = "user@example.com"
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultDataSourceSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchCriteriaSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultEmployeeRankingSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchResultsFilteringSettings(true)).thenCallRealMethod()
    val defaultSettings = mockConfigurationService.getDefaultSearchSettings()

    when(EmployeeSearchServiceTest.mockHRDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(java.util.Optional.empty[HRDataEmployeeProfile]())
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeProfileVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeInferredRolesVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getSearchSettings(authenticated_user)).thenReturn(defaultSettings)

    when(EmployeeSearchServiceTest.mockEmployeeProfileRepository.findById(any[EmployeeIdentity])).thenReturn(Some(fullM365EmployeeProfile).asJava)
    when(EmployeeSearchServiceTest.mockEmployeeInferredRolesRepository.findByEmailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(Optional.empty().asInstanceOf[Optional[EmployeeInferredRoles]])

    // Act
    val employee: Option[Employee] = EmployeeSearchServiceTest.service.getEmployee("1", authenticated_user)

    // Assert
    Assert.assertTrue("Returned employee should be defined", employee.isDefined)
    Assert.assertEquals("Id should match", fullM365EmployeeProfile.composedId.id, employee.get.id)
    Assert.assertEquals("Mail should match", fullM365EmployeeProfile.mail, employee.get.mail)
    Assert.assertEquals("Name should match", fullM365EmployeeProfile.displayName, employee.get.name)
    Assert.assertEquals("AboutMe should match", fullM365EmployeeProfile.aboutMe, employee.get.about)
    Assert.assertEquals("Role should match", fullM365EmployeeProfile.jobTitle, employee.get.role)
    Assert.assertEquals("Location should match", fullM365EmployeeProfileLocation, employee.get.location)
    Assert.assertEquals("ReportsTo should match", fullM365EmployeeProfile.reportsTo, employee.get.reportsTo)
    Assert.assertEquals("ManagerEmail should match", fullM365EmployeeProfile.managerEmail, employee.get.managerEmail)
    Assert.assertNull("CurrentEngagement should be null", employee.get.currentEngagement)
    Assert.assertNull("Availability should be null", employee.get.availableSince)
    Assert.assertNull("LinkedInProfile should be null", employee.get.linkedInProfile)
  }

  @Test
  def testGetMinimalM365EmployeeWithoutHRData(): Unit = {
    // Arrange
    val authenticated_user = "user@example.com"
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultDataSourceSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchCriteriaSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultEmployeeRankingSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchResultsFilteringSettings(true)).thenCallRealMethod()
    val defaultSettings = mockConfigurationService.getDefaultSearchSettings()

    when(EmployeeSearchServiceTest.mockHRDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(java.util.Optional.empty[HRDataEmployeeProfile]())
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeProfileVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeInferredRolesVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getSearchSettings(authenticated_user)).thenReturn(defaultSettings)

    when(EmployeeSearchServiceTest.mockEmployeeProfileRepository.findById(any[EmployeeIdentity])).thenReturn(Some(minimalM365EmployeeProfile).asJava)
    when(EmployeeSearchServiceTest.mockEmployeeInferredRolesRepository.findByEmailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(Optional.empty().asInstanceOf[Optional[EmployeeInferredRoles]])

    // Act
    val employee: Option[Employee] = EmployeeSearchServiceTest.service.getEmployee("1", authenticated_user)

    // Assert
    Assert.assertTrue("Returned employee should be defined", employee.isDefined)
    Assert.assertEquals("Id should match", minimalM365EmployeeProfile.composedId.id, employee.get.id)
    Assert.assertEquals("Mail should match", minimalM365EmployeeProfile.mail, employee.get.mail)
    Assert.assertEquals("Name should match", minimalM365EmployeeProfile.displayName, employee.get.name)
    Assert.assertEquals("AboutMe should match", minimalM365EmployeeProfile.aboutMe, employee.get.about)
    Assert.assertEquals("Role should match", minimalM365EmployeeProfile.jobTitle, employee.get.role)
    Assert.assertEquals("Location should match", minimalM365EmployeeProfileLocation, employee.get.location)
    Assert.assertEquals("ReportsTo should match", minimalM365EmployeeProfile.reportsTo, employee.get.reportsTo)
    Assert.assertEquals("ManagerEmail should match", minimalM365EmployeeProfile.managerEmail, employee.get.managerEmail)
    Assert.assertNull("CurrentEngagement should be null", employee.get.currentEngagement)
    Assert.assertNull("Availability should be null", employee.get.availableSince)
    Assert.assertNull("LinkedInProfile should be null", employee.get.linkedInProfile)
  }

  @Test
  def testGetFullM365EmployeeWithHRData(): Unit = {
    // Arrange
    val authenticated_user = "user@example.com"
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultDataSourceSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchCriteriaSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultEmployeeRankingSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchResultsFilteringSettings(true)).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchResultsFilteringSettings(false)).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.isM365HighestPriorityDataSource()).thenReturn(true)


    val defaultSettings = mockConfigurationService.getDefaultSearchSettings()

    when(EmployeeSearchServiceTest.mockHRDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(Some(fullHRDataEmployeeProfile).asJava)
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeProfileVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeInferredRolesVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getSearchSettings(authenticated_user)).thenReturn(defaultSettings)

    when(EmployeeSearchServiceTest.mockEmployeeProfileRepository.findById(any[EmployeeIdentity])).thenReturn(Some(fullM365EmployeeProfile).asJava)
    when(EmployeeSearchServiceTest.mockEmployeeInferredRolesRepository.findByEmailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(Optional.empty().asInstanceOf[Optional[EmployeeInferredRoles]])
    // Act
    val employee: Option[Employee] = EmployeeSearchServiceTest.service.getEmployee("1", authenticated_user)

    // Assert
    Assert.assertTrue("Returned employee should be defined", employee.isDefined)
    Assert.assertEquals("Id should match", fullM365EmployeeProfile.composedId.id, employee.get.id)
    Assert.assertEquals("Mail should match", fullM365EmployeeProfile.mail, employee.get.mail)
    Assert.assertEquals("Name should match M365", fullM365EmployeeProfile.displayName, employee.get.name)
    Assert.assertEquals("AboutMe should match", fullM365EmployeeProfile.aboutMe, employee.get.about)
    Assert.assertEquals("Role should match M365", fullM365EmployeeProfile.jobTitle, employee.get.role)
    Assert.assertEquals("Location should match M365", fullM365EmployeeProfileLocation, employee.get.location)
    Assert.assertEquals("ReportsTo should match M365", fullM365EmployeeProfile.reportsTo, employee.get.reportsTo)
    Assert.assertEquals("ManagerEmail should match M365", fullM365EmployeeProfile.managerEmail, employee.get.managerEmail)
    Assert.assertEquals("CurrentEngagement should match HRData", fullHRDataEmployeeProfile.currentEngagement, employee.get.currentEngagement)
    Assert.assertEquals("Availability should match HRData", fullHRDataEmployeeProfile.availableStartingFrom, employee.get.availableSince)
    Assert.assertEquals("LinkedInProfile should match HRData", fullHRDataEmployeeProfile.linkedInProfile, employee.get.linkedInProfile)
  }


  @Test
  def testGetMinimalM365EmployeeWithHRData(): Unit = {
    // Arrange
    val authenticated_user = "user@example.com"
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultDataSourceSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchCriteriaSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultEmployeeRankingSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchResultsFilteringSettings(true)).thenCallRealMethod()
    val defaultSettings = mockConfigurationService.getDefaultSearchSettings()

    when(EmployeeSearchServiceTest.mockHRDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(Some(fullHRDataEmployeeProfile).asJava)
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeProfileVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeInferredRolesVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getSearchSettings(authenticated_user)).thenReturn(defaultSettings)

    when(EmployeeSearchServiceTest.mockEmployeeProfileRepository.findById(any[EmployeeIdentity])).thenReturn(Some(minimalM365EmployeeProfile).asJava)
    when(EmployeeSearchServiceTest.mockEmployeeInferredRolesRepository.findByEmailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(Optional.empty().asInstanceOf[Optional[EmployeeInferredRoles]])
    // Act
    val employee: Option[Employee] = EmployeeSearchServiceTest.service.getEmployee("1", authenticated_user)

    // Assert
    Assert.assertTrue("Returned employee should be defined", employee.isDefined)
    Assert.assertEquals("Id should match", minimalM365EmployeeProfile.composedId.id, employee.get.id)
    Assert.assertEquals("Mail should match", minimalM365EmployeeProfile.mail, employee.get.mail)
    Assert.assertEquals("Name should match M365", minimalM365EmployeeProfile.displayName, employee.get.name)
    Assert.assertEquals("AboutMe should match", minimalM365EmployeeProfile.aboutMe, employee.get.about)
    Assert.assertEquals("Role should match HRData", fullHRDataEmployeeProfile.role, employee.get.role)
    Assert.assertEquals("Location should match HRData", fullHRDataEmployeeProfile.location, employee.get.location)
    Assert.assertEquals("ReportsTo should match HRData", fullHRDataEmployeeProfile.managerName, employee.get.reportsTo)
    Assert.assertEquals("ManagerEmail should match HRData", fullHRDataEmployeeProfile.managerEmail, employee.get.managerEmail)
    Assert.assertEquals("CurrentEngagement should match HRData", fullHRDataEmployeeProfile.currentEngagement, employee.get.currentEngagement)
    Assert.assertEquals("Availability should match HRData", fullHRDataEmployeeProfile.availableStartingFrom, employee.get.availableSince)
    Assert.assertEquals("LinkedInProfile should match HRData", fullHRDataEmployeeProfile.linkedInProfile, employee.get.linkedInProfile)
  }


  @Test
  def testGetMinimalHRDataEmployeeWithM365Data(): Unit = {
    // Arrange
    val authenticated_user = "user@example.com"
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultDataSourceSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchCriteriaSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultEmployeeRankingSearchSettings()).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.getDefaultSearchResultsFilteringSettings(false)).thenCallRealMethod()
    when(EmployeeSearchServiceTest.mockConfigurationService.isM365HighestPriorityDataSource()).thenReturn(false)
    val defaultSettings = mockConfigurationService.getDefaultSearchSettings()

    when(EmployeeSearchServiceTest.mockHRDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(Some(minimalHRDataEmployeeProfile).asJava)
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeProfileVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getLatestEmployeeInferredRolesVersionFromCache()).thenReturn(LocalDateTime.now())
    when(EmployeeSearchServiceTest.mockConfigurationService.getSearchSettings(authenticated_user)).thenReturn(defaultSettings)

    when(EmployeeSearchServiceTest.mockEmployeeProfileRepository.findById(any[EmployeeIdentity])).thenReturn(Some(fullM365EmployeeProfile).asJava)
    when(EmployeeSearchServiceTest.mockEmployeeInferredRolesRepository.findByEmailAndComposedIdVersion(any[String], any[LocalDateTime])).thenReturn(Optional.empty().asInstanceOf[Optional[EmployeeInferredRoles]])
    // Act
    val employee: Option[Employee] = EmployeeSearchServiceTest.service.getEmployee("1", authenticated_user)

    // Assert
    Assert.assertTrue("Returned employee should be defined", employee.isDefined)
    Assert.assertEquals("Id should match", fullM365EmployeeProfile.composedId.id, employee.get.id)
    Assert.assertEquals("Mail should match", fullM365EmployeeProfile.mail, employee.get.mail)
    Assert.assertEquals("Name should match M365", fullM365EmployeeProfile.displayName, employee.get.name)
    Assert.assertEquals("AboutMe should match", fullM365EmployeeProfile.aboutMe, employee.get.about)
    Assert.assertEquals("Role should match M365", fullM365EmployeeProfile.jobTitle, employee.get.role)
    Assert.assertEquals("Location should match M365", fullM365EmployeeProfileLocation, employee.get.location)
    Assert.assertEquals("ReportsTo should match M365", fullM365EmployeeProfile.reportsTo, employee.get.reportsTo)
    Assert.assertEquals("ManagerEmail should match M365", fullM365EmployeeProfile.managerEmail, employee.get.managerEmail)
    Assert.assertEquals("CurrentEngagement should match HRData", minimalHRDataEmployeeProfile.currentEngagement, employee.get.currentEngagement)
    Assert.assertEquals("Availability should match HRData", minimalHRDataEmployeeProfile.availableStartingFrom, employee.get.availableSince)
    Assert.assertEquals("LinkedInProfile should match HRData", minimalHRDataEmployeeProfile.linkedInProfile, employee.get.linkedInProfile)
  }

}

object EmployeeSearchServiceTest {
  var mockEmployeeProfileRepository: EmployeeProfileRepository = _
  var mockHRDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _
  var mockConfigurationService: ConfigurationService = _
  var mockEmployeeInferredRolesRepository: EmployeeInferredRolesRepository = _
  var service: EmployeeSearchService = _

  val fullM365EmployeeProfile = new EmployeeProfile()
  fullM365EmployeeProfile.composedId = EmployeeIdentity("1", LocalDateTime.now())
  fullM365EmployeeProfile.mail = "johndoe@ex.com"
  fullM365EmployeeProfile.displayName = "John Doe"
  fullM365EmployeeProfile.aboutMe = "Story of my life"
  fullM365EmployeeProfile.jobTitle = "Software Engineer"
  fullM365EmployeeProfile.city = "Bellevue"
  fullM365EmployeeProfile.state = "WA"
  fullM365EmployeeProfile.country = "US"
  fullM365EmployeeProfile.reportsTo = "Jane Doe"
  fullM365EmployeeProfile.managerEmail = "janedoe@ex.com"
  val fullM365EmployeeProfileLocation = "Bellevue, WA, US"

  val minimalM365EmployeeProfile = new EmployeeProfile()
  minimalM365EmployeeProfile.composedId = EmployeeIdentity("1", LocalDateTime.now())
  minimalM365EmployeeProfile.mail = "johndoe@ex.com"
  minimalM365EmployeeProfile.displayName = "John Doe"
  minimalM365EmployeeProfile.aboutMe = ""
  minimalM365EmployeeProfile.jobTitle = ""
  minimalM365EmployeeProfile.city = ""
  minimalM365EmployeeProfile.state = ""
  minimalM365EmployeeProfile.country = ""
  minimalM365EmployeeProfile.reportsTo = ""
  minimalM365EmployeeProfile.managerEmail = ""
  val minimalM365EmployeeProfileLocation = ""

  val fullHRDataEmployeeProfile = new HRDataEmployeeProfile()
  fullHRDataEmployeeProfile.composedId = HRDataEmployeeIdentity("johndoe@ex.com", LocalDateTime.now())
  fullHRDataEmployeeProfile.name = "john  doe"
  fullHRDataEmployeeProfile.location = "Seattle, WA, US"
  fullHRDataEmployeeProfile.availableStartingFrom = "2021-06-30"
  fullHRDataEmployeeProfile.currentEngagement = "Project X"
  fullHRDataEmployeeProfile.managerName = "Joana Doe"
  fullHRDataEmployeeProfile.managerEmail = "joanadoe@ex.com"
  fullHRDataEmployeeProfile.role = "SW Eng"
  fullHRDataEmployeeProfile.linkedInProfile = "https://www.linkedin.com/in/test_profile"

  val minimalHRDataEmployeeProfile = new HRDataEmployeeProfile()
  minimalHRDataEmployeeProfile.composedId = HRDataEmployeeIdentity("johndoe@ex.com", LocalDateTime.now())
  minimalHRDataEmployeeProfile.name = "john  doe"
  minimalHRDataEmployeeProfile.location = ""
  minimalHRDataEmployeeProfile.availableStartingFrom = ""
  minimalHRDataEmployeeProfile.currentEngagement = ""
  minimalHRDataEmployeeProfile.managerName = ""
  minimalHRDataEmployeeProfile.managerEmail = ""
  minimalHRDataEmployeeProfile.role = ""
  minimalHRDataEmployeeProfile.linkedInProfile = ""


  @BeforeClass
  def setup() {
    mockEmployeeProfileRepository = mock(classOf[EmployeeProfileRepository])
    mockHRDataEmployeeProfileRepository = mock(classOf[HRDataEmployeeProfileRepository])
    mockConfigurationService = mock(classOf[ConfigurationService])
    mockEmployeeInferredRolesRepository = mock(classOf[EmployeeInferredRolesRepository])

    service = new EmployeeSearchService()
    service.configurationService = mockConfigurationService
    service.hrDataEmployeeProfileRepository = mockHRDataEmployeeProfileRepository
    service.employeeProfileRepository = mockEmployeeProfileRepository
    service.employeeInferredRolesRepository = mockEmployeeInferredRolesRepository
  }
}
