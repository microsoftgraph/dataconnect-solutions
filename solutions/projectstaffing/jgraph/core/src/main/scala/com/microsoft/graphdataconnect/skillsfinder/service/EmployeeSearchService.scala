/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeInferredRoles, EmployeeProfile, RecommendedEmployee}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.HRDataEmployeeProfile
import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.DataSourceType.M365
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee.{EmployeeInferredRolesRepository, EmployeeProfileRepository}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata.HRDataEmployeeProfileRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.team.TeamMemberRepository
import com.microsoft.graphdataconnect.skillsfinder.models._
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.recommendation.EmployeeRecommendation
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.{Employee, EmployeeResponseWrapper, EmployeeSearchCriteria, EmployeeSearchQuery}
import com.microsoft.graphdataconnect.skillsfinder.service.EmployeeSearchService.{EmployeeImplicits, EmployeeProfileImplicits}
import com.microsoft.graphdataconnect.skillsfinder.service.search.{AvailabilityPaginationService, NameSearchService, RecommendationService, RelevancePaginationService}
import com.microsoft.graphdataconnect.skillsfinder.utils.StringUtils._
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap

@Service
class EmployeeSearchService {

  private val logger: Logger = LoggerFactory.getLogger(classOf[EmployeeSearchService])

  @Autowired
  var employeeProfileRepository: EmployeeProfileRepository = _

  @Autowired
  var hrDataEmployeeProfileRepository: HRDataEmployeeProfileRepository = _

  @Autowired
  var employeeInferredRolesRepository: EmployeeInferredRolesRepository = _

  @Autowired
  var teamMemberRepository: TeamMemberRepository = _

  @Autowired
  var nameSearchService: NameSearchService = _

  @Autowired
  var recommendationService: RecommendationService = _

  @Value("${app.relevantSkillsSize}")
  var relevantSkillsSize = 20

  @Autowired
  var configurationService: ConfigurationService = _

  @Autowired
  var relevancePaginationService: RelevancePaginationService = _

  @Autowired
  var availabilityPaginationService: AvailabilityPaginationService = _

  @Autowired
  var mapper: ObjectMapper = _

  def getEmployee(id: String, userId: String): Option[Employee] = {
    val latestEmployeeProfileVersion = configurationService.getLatestEmployeeProfileVersionFromCache()
    val searchSettings: SearchSettingsRequestResponse = configurationService.getSearchSettings(userId)
    val isM365PrimaryDataSource = searchSettings.dataSourceSettings.dataSourcesPriority.head.equals(M365)
    Option(employeeProfileRepository.findById(EmployeeIdentity(id, latestEmployeeProfileVersion)).orElse(null))
      .map((employeeProfile: EmployeeProfile) => {
        val employee: Employee = employeeProfile.toEmployee()

        val hrDataEmployeeProfileOpt: Option[HRDataEmployeeProfile] = findHRDataEmployeeProfile(employee.mail)
        var enhancedEmployee = hrDataEmployeeProfileOpt
          .map(hrDataEmployeeProfile => employee.enhancedWithHRData(hrDataEmployeeProfile, isM365PrimaryDataSource))
          .getOrElse(employee)

        val inferredRolesOpt: Option[EmployeeInferredRoles] = findEmployeeInferredRoles(employee.mail)
        enhancedEmployee = inferredRolesOpt
          .map((inferredRoles: EmployeeInferredRoles) => inferredRoles.mergeToSingleRolesList())
          .map(roles => enhancedEmployee.copy(inferredRoles = roles))
          .getOrElse(enhancedEmployee)

        enhancedEmployee
      })
  }

  def getRecommendedEmployees(employeeSearchQuery: EmployeeSearchQuery, size: Int, offset: Int, userId: String): EmployeeResponseWrapper = {
    val getRecommendedEmployeesStartTime = System.nanoTime()
    val (reachedEndOfResults, relevantEmployees) = if (employeeSearchQuery.searchTerms.isEmpty) {
      val storedProcedureFilters: Seq[StoredProcedureFilter] = employeeSearchQuery.searchFilterValues.map {
        case (filterType, filterValues) =>
          val searchResultsFilter = SearchResultsFilter.fromType(filterType)
          StoredProcedureFilter(dbTable = searchResultsFilter.dbTable, dbColumn = searchResultsFilter.dbColumn, filterValues)
      }.toList
      val storedProcedureJsonFilter = mapper.writeValueAsString(storedProcedureFilters)

      val storedProcedureStartTime = System.nanoTime()
      val rawEmployeeRows: Seq[RecommendedEmployee] = employeeProfileRepository.findRecommendedEmployees(
        size,
        offset,
        orderByAvailabilityAndName = true,
        employeeSearchQuery.requiredAvailability.availableAtTheLatestOn,
        userId,
        "",
        TimeUtils.localDateTimeToString(configurationService.getLatestEmployeeProfileVersion()),
        TimeUtils.localDateTimeToString(configurationService.getLatestHRDataEmployeeProfileVersionFromCache()),
        TimeUtils.localDateTimeToString(configurationService.getLatestEmployeeInferredRolesVersion()),
        configurationService.isHRDataMandatory(userId), //job title Engineer
        storedProcedureJsonFilter).asScala

      val storedProcedureDiffTime = (System.nanoTime() - storedProcedureStartTime) / 1000000
      logger.info(s"[stored procedure with filtering][total_time]: $storedProcedureDiffTime millis")

      logger.debug(s"The pagination stored procedure returned ${rawEmployeeRows.length} rows")
      val relevantEmployees: Seq[(RecommendedEmployee, List[String])] = parseJsonSkillsFromEmployees(rawEmployeeRows)
      logger.info(s"Found profile information about ${relevantEmployees.length} employees")
      val relevantEmployeesWithSkills: Seq[Employee] = relevantEmployees.map { case (rawEmployeeRow, declaredSkills) =>
        rawEmployeeRow.toEmployee(declaredSkills)
      }

      (findTotalEmployeesRows(rawEmployeeRows) <= offset + size, relevantEmployeesWithSkills)
    } else {
      val searchSettings = configurationService.getSearchSettings(userId).updateTaxonomyList(employeeSearchQuery.taxonomiesList)

      val searchStartTime = System.nanoTime()
      val (reachedEndOfResults: Boolean, employeeRecommendations: List[EmployeeRecommendation]) = employeeSearchQuery.searchCriteria match {
        case EmployeeSearchCriteria.SKILLS => {
          employeeSearchQuery.sortBy match {
            case OrderingType.AVAILABILITY => availabilityPaginationService.getRecommendedEmployeesOrderedByAvailability(employeeSearchQuery, searchSettings, size, offset, userId)
            case OrderingType.RELEVANCE => relevancePaginationService.getRecommendedEmployees(employeeSearchQuery.searchTerms, searchSettings, employeeSearchQuery.searchFilterValues, size, offset, employeeSearchQuery.requiredAvailability.availableAtTheLatestOn)
          }
        }
        case EmployeeSearchCriteria.NAME => (true, nameSearchService.getRecommendedEmployeesByName(employeeSearchQuery.searchTerms, employeeSearchQuery.searchFilterValues))
        case _ => throw new IllegalArgumentException(s"Invalid employee search criteria ${employeeSearchQuery.searchCriteria}")
      }

      val searchDiffTime = (System.nanoTime() - searchStartTime) / 1000000

      logger.info(s"[recommend employees using AzureSearch][total_time]: $searchDiffTime millis")

      val emailsForWhichToExtractProfiles = if (employeeRecommendations.nonEmpty) {
        employeeRecommendations.map(rec => "'" + rec.email + "'").mkString(",")
      } else null

      logger.info(s"The Engine recommended ${employeeRecommendations.length} employees")

      val engineRecommendationsByEmail: Map[String, EmployeeRecommendation] = employeeRecommendations.groupBy(_.email)
        .map((emailToRecommendations: (String, List[EmployeeRecommendation])) => (emailToRecommendations._1.trim.toLowerCase, emailToRecommendations._2.head))

      val storedProcedureStartTime = System.nanoTime()
      val rawEmployeeRows: Seq[RecommendedEmployee] = employeeProfileRepository.findRecommendedEmployees(
        size,
        0,
        orderByAvailabilityAndName = false,
        "",
        userId,
        emailsForWhichToExtractProfiles,
        TimeUtils.localDateTimeToString(configurationService.getLatestEmployeeProfileVersionFromCache()),
        TimeUtils.localDateTimeToString(configurationService.getLatestHRDataEmployeeProfileVersionFromCache()),
        TimeUtils.localDateTimeToString(configurationService.getLatestEmployeeInferredRolesVersion()),
        configurationService.isHRDataMandatory(userId),
        "").asScala

      val storedProcedureDiffTime = (System.nanoTime() - storedProcedureStartTime) / 1000000

      logger.info(s"[stored procedure][total_time]: $storedProcedureDiffTime millis")
      logger.debug(s"The pagination stored procedure returned ${rawEmployeeRows.length} rows")

      val relevantEmployees: Seq[(RecommendedEmployee, List[String])] = parseJsonSkillsFromEmployees(rawEmployeeRows)

      logger.info(s"Found profile information about ${relevantEmployees.length} employees")

      val relevantEmployeesWithSkills: Seq[Employee] = relevantEmployees.map { case (rawEmployeeRow, declaredSkills) =>
        val employeeRecommendation: EmployeeRecommendation = engineRecommendationsByEmail(rawEmployeeRow.mail.toLowerCase.trim)
        val employee: Employee = rawEmployeeRow.toEmployee(declaredSkills)
        val employeeWithSkills = employee.enhancedWithInferredInformationLists(employeeRecommendation, Some(employeeSearchQuery.taxonomiesList))
        Employee.dropEmptyElementsFromLists(employeeWithSkills)
      }

      (reachedEndOfResults, relevantEmployeesWithSkills)
    }

    logger.info(s"Recommending ${relevantEmployees.size} employees. Used size: $size, offset: $offset")
    val getRecommendedEmployeesDiffTime = (System.nanoTime() - getRecommendedEmployeesStartTime) / 1000000
    logger.info(s"[getRecommendedEmployees][total_time]: $getRecommendedEmployeesDiffTime millis")
    EmployeeResponseWrapper(reachedEndOfResults, relevantEmployees)
  }

  def getFacets(userId: String): Seq[SearchFilterValues] = {
    logger.trace("Asking recommendation engine for employee search facets")
    val searchSettings: SearchSettingsRequestResponse = configurationService.getSearchSettings(userId)
    val filterFields: Seq[FilterType] = searchSettings.searchResultsFilters
      .flatMap(_.filters).filter(_.isActive).map(_.filterType)

    recommendationService.retrieveSearchFilterValues(filterFields)
  }

  private def parseJsonSkillsFromEmployees(employeeRows: Seq[RecommendedEmployee]): Seq[(RecommendedEmployee, List[String])] = {
    employeeRows.map { employeeRow =>
      val skills = if (null != employeeRow.skills_json) {
        mapper.readValue(employeeRow.skills_json, classOf[java.util.List[String]]).asScala.toList
      } else List.empty
      (employeeRow, skills)
    }
  }

  private def findTotalEmployeesRows(employeeRows: Seq[RecommendedEmployee]): Int = {
    if (employeeRows.nonEmpty) employeeRows.head.total else 0
  }

  // TODO move this as well as the findHRDataEmployeeProfile method in TeamService to a common class; use the new method instead
  private def findHRDataEmployeeProfile(email: String): Option[HRDataEmployeeProfile] = {
    if (!email.isNullOrBlank) {
      Option(hrDataEmployeeProfileRepository.findByComposedIdMailAndComposedIdVersion(email, configurationService.getLatestHRDataEmployeeProfileVersionFromCache()).orElse(null))
    } else {
      logger.warn(s"Employee with email $email is present in M365, but not in HR Data")
      None
    }
  }

  def findEmployeeInferredRoles(email: String): Option[EmployeeInferredRoles] = {
    if (!email.isNullOrBlank) {
      Option(employeeInferredRolesRepository.findByEmailAndComposedIdVersion(email, configurationService.getLatestEmployeeInferredRolesVersionFromCache()).orElse(null))
    } else {
      logger.warn(s"No inferred roles found from employee with email address ${email}")
      None
    }
  }

}

object EmployeeSearchService {

  // Sort the employees so that they are sorted ascending by availability (nulls first) and for equal availability they are sorted ascending by name
  def sortEmployeesByAvailabilityAndName(employees: List[Employee]): List[Employee] = {
    employees.map { employee => (employee, Option(employee.availableSince)) } // Convert fields containing nulls to Option to allow sorting on them
      .sortBy { case (employee, _) => employee.name }
      .sortBy { case (_, availableSinceOpt) => availableSinceOpt } // For ascending sort by an Option field, fields with value None come first
      .map { case (employee, availableSinceOpt) => employee }
  }

  implicit class EmployeeProfileImplicits(employeeProfile: EmployeeProfile) {
    def toEmployee(): Employee = {
      val employeeLocation: String = computeM365EmployeeLocation(city = employeeProfile.city, state = employeeProfile.state, country = employeeProfile.country)

      val declaredSkills = if (employeeProfile.skills != null) {
        employeeProfile.skills.asScala.toList.map(_.skill)
      } else List.empty[String]

      val responsibilities = if (employeeProfile.responsibilities != null) {
        employeeProfile.responsibilities.asScala.toList.map(_.responsibility)
      } else List.empty[String]

      Employee(id = employeeProfile.composedId.id,
        name = employeeProfile.displayName,
        mail = employeeProfile.mail.trim,
        role = employeeProfile.jobTitle,
        inferredRoles = List.empty,
        location = employeeLocation,
        about = employeeProfile.aboutMe,
        relevantSkills = declaredSkills,
        inferredSkills = null,
        declaredSkills = declaredSkills,
        domainToSkillMap = Map.empty[String, List[String]],
        highlightedTerms = null,
        topics = responsibilities,
        profilePicture = employeeProfile.profilePicture,
        reportsTo = employeeProfile.reportsTo,
        managerEmail = employeeProfile.managerEmail,
        currentEngagement = null,
        department = employeeProfile.department,
        availableSince = null,
        linkedInProfile = null)
    }

  }

  implicit class EmployeeImplicits(employee: Employee) {
    def enhancedWithInferredInformationLists(employeeRecommendation: EmployeeRecommendation,
                                             requiredTaxonomyOrderOpt: Option[List[String]] = None): Employee = {
      val domainToSkillsMap: Map[String, List[String]] = if (requiredTaxonomyOrderOpt.isDefined && requiredTaxonomyOrderOpt.get.nonEmpty) {
        sortInferredSkillsMapByTaxonomyList(employeeRecommendation.inferredSkillsFromTaxonomy, requiredTaxonomyOrderOpt.get)
      } else {
        val defaultTaxonomyOrder = TaxonomyType.values.toList.map(_.toString)
        sortInferredSkillsMapByTaxonomyList(employeeRecommendation.inferredSkillsFromTaxonomy, defaultTaxonomyOrder)
      }

      employee.copy(
        relevantSkills = employeeRecommendation.relevantSkills,
        inferredSkills = employeeRecommendation.inferredSkills,
        domainToSkillMap = domainToSkillsMap,
        //      declaredSkills = employeeRecommendation.profile_skills,
        highlightedTerms = employeeRecommendation.highlightedTerms)
    }

    /**
     * Sort the inferredSkillsFromTaxonomy map in the order imposed by requiredTaxonomyOrder
     *
     * @param inferredSkillsFromTaxonomy map from taxonomy to inferred skills, which is to be sorted by key
     * @param requiredTaxonomyOrder      the order the taxonomies which is to be imposed on the keys in the resulting map
     * @return the sorted map
     */
    private def sortInferredSkillsMapByTaxonomyList(inferredSkillsFromTaxonomy: Map[String, List[String]],
                                                    requiredTaxonomyOrder: List[String]): Map[String, List[String]] = {
      if (inferredSkillsFromTaxonomy.nonEmpty) {
        val orderedInferredSkillsByTaxonomy = requiredTaxonomyOrder.flatMap { taxonomyType =>
          val inferredSkillsForTaxonomyOpt: Option[List[String]] = inferredSkillsFromTaxonomy.get(taxonomyType)
          inferredSkillsForTaxonomyOpt.map(inferredSkillsForTaxonomy => (taxonomyType, inferredSkillsForTaxonomy))
        }
        ListMap.empty ++ orderedInferredSkillsByTaxonomy
      } else {
        inferredSkillsFromTaxonomy
      }
    }

    def enhancedWithHRData(hrDataEmployeeProfile: HRDataEmployeeProfile, isM365PrimaryDataSource: Boolean): Employee = {
      val employeeLocation: String = if (employee.location.isNullOrBlank || (!isM365PrimaryDataSource && !hrDataEmployeeProfile.location.isNullOrBlank)) {
        hrDataEmployeeProfile.location
      } else {
        employee.location
      }

      val employeeRole: String = if (employee.role.isNullOrBlank || (!isM365PrimaryDataSource && !hrDataEmployeeProfile.role.isNullOrBlank)) {
        hrDataEmployeeProfile.role
      } else {
        employee.role
      }

      val employeeReportsTo = if (employee.reportsTo.isNullOrEmpty || (!isM365PrimaryDataSource && !hrDataEmployeeProfile.managerName.isNullOrBlank)) {
        hrDataEmployeeProfile.managerName
      } else {
        employee.reportsTo
      }

      val employeeManagerEmail = if (employee.managerEmail.isNullOrEmpty || (!isM365PrimaryDataSource && !hrDataEmployeeProfile.managerEmail.isNullOrBlank)) {
        hrDataEmployeeProfile.managerEmail
      } else {
        employee.managerEmail
      }

      employee.copy(
        role = employeeRole,
        location = employeeLocation,
        currentEngagement = hrDataEmployeeProfile.currentEngagement,
        reportsTo = employeeReportsTo,
        managerEmail = employeeManagerEmail,
        availableSince = hrDataEmployeeProfile.availableStartingFrom,
        linkedInProfile = hrDataEmployeeProfile.linkedInProfile)
    }
  }

  // TODO move this to a common class; use the new method instead
  def computeM365EmployeeLocation(city: String, state: String, country: String): String = {
    Array(city, state, country).filterNot(s => s.isNullOrBlank).mkString(", ")
  }

}
