package com.microsoft.graphdataconnect.skillsfinder.service.search

import java.time.format.DateTimeFormatter

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.{SearchCriterion, SearchCriterionType}
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.EmployeeSearchQuery
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.recommendation.EmployeeRecommendation
import com.microsoft.graphdataconnect.skillsfinder.models.business.{AvailabilitySearchStartPoint, SearchQueryIdentifier}
import com.microsoft.graphdataconnect.skillsfinder.models.{FilterType, SearchSettingsRequestResponse, TaxonomyType}
import org.ehcache.Cache
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

import scala.collection.mutable

@Service
class AvailabilityPaginationService extends PaginationService {

  private val log: Logger = LoggerFactory.getLogger(classOf[AvailabilityPaginationService])

  @Value("${azure.search.emailsIndex}")
  var emailsIndexName = "gdc-mails-20201014"

  @Autowired
  var availabilityPaginationCache: Cache[SearchQueryIdentifier, AvailabilitySearchStartPoint] = _

  def getRecommendedEmployeesOrderedByAvailability(employeeSearchQuery: EmployeeSearchQuery,
                                                   searchSettings: SearchSettingsRequestResponse,
                                                   size: Int,
                                                   offset: Int,
                                                   userId: String): (Boolean, List[EmployeeRecommendation]) = {

    var selectedEmployeeListComingFromSearch: mutable.LinkedHashMap[String, EmployeeRecommendation] = new scala.collection.mutable.LinkedHashMap[String, EmployeeRecommendation]

    val (searchRelevanceWeight: Double, searchFreshnessWeight: Double, searchVolumeWeight: Double) = RecommendationEngineUtil.extractSearchCriteriaWeight(searchSettings)


    log.info(f"[recommendation_engine][getRecommendedEmployees] ordering weights. " +
      f"relevance[${searchSettings.relevanceScoreEnabled}]: ${searchSettings.relevanceScore} " +
      f"volume[${searchSettings.volumeEnabled}]: ${searchSettings.volume} " +
      f"freshness[${searchSettings.freshnessEnabled}]: ${searchSettings.freshness}")


    log.info(f"[recommendation_engine][getRecommendedEmployees] ordering weights normalized. " +
      f"relevance[${searchSettings.relevanceScoreEnabled}]: $searchRelevanceWeight " +
      f"volume[${searchSettings.volumeEnabled}]: $searchVolumeWeight" +
      f"freshness[${searchSettings.freshnessEnabled}]: $searchFreshnessWeight")

    val additionalSourceTypes = new mutable.ListBuffer[SearchCriterionType]()

    var hierarchyOrderScore = 11

    val taxonomyList = searchSettings.taxonomyList

    val deExtendedSearchRequested = taxonomyList.nonEmpty
    val useEmailsAsSearchCriterion = searchSettings.searchCriteria.contains(SearchCriterion(SearchCriterionType.EMAIL_CONTENT, true))

    val useReceivedEmailsAsSearchCriterion = (searchSettings.searchCriteria.contains(SearchCriterion(SearchCriterionType.RECEIVED_EMAIL_CONTENT, true))
      && useEmailsAsSearchCriterion) ||
      (searchSettings.useReceivedEmailsContent && useEmailsAsSearchCriterion)

    val profileSearchCriterionSelected = searchSettings.
      searchCriteria.
      filter(_.isActive).
      exists(sc =>
        List(SearchCriterion(SearchCriterionType.PROFILE_ABOUT_ME, true),
          SearchCriterion(SearchCriterionType.PROFILE_SKILLS, true),
          SearchCriterion(SearchCriterionType.PROFILE_TOPICS, true)).contains(sc))

    val searchQueryIdentifier = SearchQueryIdentifier.from(employeeSearchQuery, top = size, skip = offset, userId = userId)

    val cachedStartPoint: Option[AvailabilitySearchStartPoint] = Option(availabilityPaginationCache.get(searchQueryIdentifier))

    //if we don't have cached the starting point/limit
    // then we have to get all the employees that match the search and return them only starting from the offset
    val (top: Int, skip: Int) = if (offset > 0 & cachedStartPoint.nonEmpty) (size, 0) else (offset + size, offset)

    var requestedEmployeeAddresses: mutable.Set[String] = mutable.Set()

    val requiredSkills: List[String] = employeeSearchQuery.searchTerms

    val availableAtTheLatestOn: Option[String] = Option(employeeSearchQuery.requiredAvailability.availableAtTheLatestOn)

    val availabilityStartLimit: Option[String] = cachedStartPoint.flatMap(_.availabilityStartLimit)

    val employeeEmailAddressesToBeIgnored: mutable.Set[String] = if (cachedStartPoint.nonEmpty) mutable.Set[String]() ++ cachedStartPoint.get.ignoreEmailAddresses else mutable.Set[String]()

    val searchFilterValues: Map[FilterType, List[String]] = employeeSearchQuery.searchFilterValues

    def searchForProfileCriterion(searchCriterion: SearchCriterionType, fieldName: String): Unit = {
      if (requestedEmployeeAddresses.nonEmpty) {
        val (_, employeeRecommendations: List[EmployeeRecommendation]) = performSearchOnUserIndex(requiredSkills,
          fieldName = fieldName,
          stage = searchCriterion,
          givenScore = hierarchyOrderScore,
          top = requestedEmployeeAddresses.size,
          taxonomyList = taxonomyList,
          searchFilterValues = searchFilterValues,
          employeeAddressesToBeIgnored = Set(),
          requestedEmployeeAddresses = requestedEmployeeAddresses.toSet)

        selectedEmployeeListComingFromSearch = updateEmployeeRecommendationWithNewInformation(selectedEmployeeListComingFromSearch, employeeRecommendations)
      }

      val (_: Set[String], employeeRecommendations: List[EmployeeRecommendation]) = performSearchOnUserIndex(requiredSkills,
        fieldName = fieldName,
        stage = searchCriterion,
        givenScore = hierarchyOrderScore,
        top = top,
        taxonomyList = taxonomyList,
        searchFilterValues = searchFilterValues,
        employeeAddressesToBeIgnored = employeeEmailAddressesToBeIgnored.toSet,
        requestedEmployeeAddresses = Set(),
        availableAtTheLatestOn = availableAtTheLatestOn,
        availabilityStartLimit = availabilityStartLimit,
        sortByAvailabilityAndName = true)

      employeeRecommendations.foreach(employeeRecommendation => selectedEmployeeListComingFromSearch.put(employeeRecommendation.email, employeeRecommendation))

      employeeEmailAddressesToBeIgnored ++= employeeRecommendations.map(_.email)

      requestedEmployeeAddresses ++= employeeRecommendations.map(_.email).toSet

      hierarchyOrderScore -= 1

    }

    val isEmailContentSearchCriteriaActivated = searchSettings.searchCriteria
      .exists((searchCriterion: SearchCriterion) => searchCriterion.searchCriterionType.equals(SearchCriterionType.EMAIL_CONTENT) && searchCriterion.isActive)

    val (facets: Set[String], employeesThatMatchFilter: List[EmployeeRecommendation]) = performSearchOnUserIndex(requiredSkills = List(),
      fieldName = "",
      searchFilterValues = searchFilterValues,
      availableAtTheLatestOn = availableAtTheLatestOn,
      availabilityStartLimit = availabilityStartLimit,
      sortByAvailabilityAndName = true,
      employeeAddressesToBeIgnored = cachedStartPoint.map(_.ignoreEmailAddresses.toSet).getOrElse(Set())
    )

    val mailToUserProfile: Map[String, EmployeeRecommendation] = employeesThatMatchFilter
      .map(employeeRecommendation => (employeeRecommendation.email, employeeRecommendation)).toMap

    var emailAddressesOfEmployeesThatMatchFilter: mutable.LinkedHashSet[String] = if (isEmailContentSearchCriteriaActivated) {
      new mutable.LinkedHashSet[String]() ++ employeesThatMatchFilter.map(_.email)
    } else new mutable.LinkedHashSet[String]()

    def searchForEmailContentCriterion(searchCriterion: SearchCriterionType, fieldName: String): Unit = {
      if (requestedEmployeeAddresses.nonEmpty) {

        val searchResultAsMapFromJson: Map[String, Any] = performSearchOnSendMails(requiredSkills,
          indexName = emailsIndexName,
          fieldName = fieldName,
          stage = searchCriterion,
          searchSettings = searchSettings,
          requestedEmployeeAddresses = requestedEmployeeAddresses.toSet
        )

        val mailContentRecommendations: List[EmployeeRecommendation] = buildEmployeeRecommendationsFromEmailContentAndOrderByAvailability(searchResultAsMapFromJson = searchResultAsMapFromJson,
          stage = searchCriterion,
          mailAddressesToUserProfileMap = mailToUserProfile,
          taxonomyList = taxonomyList,
          hierarchyOrderingScore = hierarchyOrderScore,
          fieldName = fieldName
        )

        selectedEmployeeListComingFromSearch = updateEmployeeRecommendationWithNewInformation(selectedEmployeeListComingFromSearch, mailContentRecommendations)
      }


      emailAddressesOfEmployeesThatMatchFilter = emailAddressesOfEmployeesThatMatchFilter.diff(employeeEmailAddressesToBeIgnored)

      if (emailAddressesOfEmployeesThatMatchFilter.nonEmpty) {
        val searchResultAsMapFromJson: Map[String, Any] = performSearchOnSendMails(requiredSkills,
          indexName = emailsIndexName,
          fieldName = fieldName,
          stage = searchCriterion,
          searchSettings = searchSettings,
          requestedEmployeeAddresses = emailAddressesOfEmployeesThatMatchFilter.toSet
        )

        var mailContentRecommendations: List[EmployeeRecommendation] = buildEmployeeRecommendationsFromEmailContentAndOrderByAvailability(searchResultAsMapFromJson = searchResultAsMapFromJson,
          stage = searchCriterion,
          mailAddressesToUserProfileMap = mailToUserProfile,
          taxonomyList = taxonomyList,
          hierarchyOrderingScore = hierarchyOrderScore,
          fieldName = fieldName,
          emailAddressesOrderByAvailability = emailAddressesOfEmployeesThatMatchFilter
        )

        mailContentRecommendations = mailContentRecommendations.slice(0, top)

        mailContentRecommendations.foreach(employeeRecommendation => selectedEmployeeListComingFromSearch.put(employeeRecommendation.email, employeeRecommendation))

        employeeEmailAddressesToBeIgnored ++= mailContentRecommendations.map(_.email)

        requestedEmployeeAddresses ++= mailContentRecommendations.map(_.email).toSet

        hierarchyOrderScore -= 1
      }

    }

    def searchForReceivedEmailContentCriterion(searchCriterionType: SearchCriterionType, fieldName: String): Unit = {

      var mailContentRecommendationsReceiver: List[EmployeeRecommendation] = List()

      if (requestedEmployeeAddresses.nonEmpty) {
        val searchResultAsMapFromJson = performSearchOnReceivedMails(requiredSkills,
          indexName = emailsIndexName,
          fieldName = fieldName,
          stage = searchCriterionType,
          searchSettings = searchSettings)

        mailContentRecommendationsReceiver = buildEmployeeRecommendationsFromReceivedEmailsContentAndOrderByAvailability(
          searchResultAsMapFromJson = searchResultAsMapFromJson,
          stage = searchCriterionType,
          mailAddressesToUserProfileMap = mailToUserProfile,
          hierarchyOrderingScore = hierarchyOrderScore,
          taxonomyList = taxonomyList,
          fieldName = fieldName,
          emailAddressesOrderByAvailability = emailAddressesOfEmployeesThatMatchFilter)

        val augmentationInfoForRequestedEmployees = mailContentRecommendationsReceiver.filter(employeeRecommendation => requestedEmployeeAddresses.contains(employeeRecommendation.email))

        selectedEmployeeListComingFromSearch = updateEmployeeRecommendationWithNewInformation(selectedEmployeeListComingFromSearch, augmentationInfoForRequestedEmployees)
      }


      emailAddressesOfEmployeesThatMatchFilter = emailAddressesOfEmployeesThatMatchFilter.diff(employeeEmailAddressesToBeIgnored)
      if (emailAddressesOfEmployeesThatMatchFilter.nonEmpty) {
        if (mailContentRecommendationsReceiver.isEmpty) {
          val searchResultAsMapFromJson = performSearchOnReceivedMails(requiredSkills,
            indexName = emailsIndexName,
            fieldName = fieldName,
            stage = searchCriterionType,
            searchSettings = searchSettings)

          mailContentRecommendationsReceiver = buildEmployeeRecommendationsFromReceivedEmailsContentAndOrderByAvailability(
            searchResultAsMapFromJson = searchResultAsMapFromJson,
            stage = searchCriterionType,
            mailAddressesToUserProfileMap = mailToUserProfile,
            hierarchyOrderingScore = hierarchyOrderScore,
            taxonomyList = taxonomyList,
            fieldName = fieldName,
            emailAddressesOrderByAvailability = emailAddressesOfEmployeesThatMatchFilter)
        }

        val employeeRecommendationsThatMatchTheFilter = mailContentRecommendationsReceiver
          .filter(employeeRecommendation => emailAddressesOfEmployeesThatMatchFilter.contains(employeeRecommendation.email))

        mailContentRecommendationsReceiver = employeeRecommendationsThatMatchTheFilter.slice(0, top)

        mailContentRecommendationsReceiver.foreach(employeeRecommendation => selectedEmployeeListComingFromSearch.put(employeeRecommendation.email, employeeRecommendation))

        requestedEmployeeAddresses ++= mailContentRecommendationsReceiver.map(_.email).toSet

        employeeEmailAddressesToBeIgnored ++= mailContentRecommendationsReceiver.map(_.email)

        hierarchyOrderScore -= 1
      }

    }

    searchSettings.searchCriteria.filter(_.isActive).foreach {
      searchCriterion =>

        searchCriterion.searchCriterionType match {
          case SearchCriterionType.PROFILE_SKILLS =>

            searchForProfileCriterion(SearchCriterionType.PROFILE_SKILLS, "skills,skills_v2,skills_lemma")

          case SearchCriterionType.PROFILE_ABOUT_ME =>

            searchForProfileCriterion(SearchCriterionType.PROFILE_ABOUT_ME, "about_me,about_me_v2,about_me_lemma")

          case SearchCriterionType.PROFILE_TOPICS =>

            searchForProfileCriterion(SearchCriterionType.PROFILE_TOPICS, "responsibilities,responsibilities_v2,responsibilities_lemma")

          case SearchCriterionType.EMAIL_CONTENT =>

            searchForEmailContentCriterion(SearchCriterionType.EMAIL_CONTENT, "Content,Content_v2,lemma_body")

          case _ =>
        }
    }


    if (profileSearchCriterionSelected && deExtendedSearchRequested) {
      additionalSourceTypes += SearchCriterionType.DE_PROFILE
    }
    if (useEmailsAsSearchCriterion && deExtendedSearchRequested) {
      additionalSourceTypes += SearchCriterionType.DE_EMAIL_CONTENT
    }
    if (useReceivedEmailsAsSearchCriterion) {
      additionalSourceTypes += SearchCriterionType.RECEIVED_EMAIL_CONTENT
    }
    if (useReceivedEmailsAsSearchCriterion && deExtendedSearchRequested) {
      additionalSourceTypes += SearchCriterionType.DE_RECEIVED_EMAIL_CONTENT
    }


    val taxoFieldLists = taxonomyList.map(p => "de_" + p.toString).mkString(",")
    additionalSourceTypes.foreach {

      case SearchCriterionType.DE_PROFILE =>

        searchForProfileCriterion(SearchCriterionType.DE_PROFILE, taxoFieldLists)

      case SearchCriterionType.DE_EMAIL_CONTENT =>

        searchForEmailContentCriterion(SearchCriterionType.DE_EMAIL_CONTENT, taxoFieldLists)

      case SearchCriterionType.RECEIVED_EMAIL_CONTENT =>

        searchForReceivedEmailContentCriterion(SearchCriterionType.RECEIVED_EMAIL_CONTENT, "Content,Content_v2,lemma_body")

      case SearchCriterionType.DE_RECEIVED_EMAIL_CONTENT =>

        searchForReceivedEmailContentCriterion(SearchCriterionType.DE_RECEIVED_EMAIL_CONTENT, taxoFieldLists)

      case _ =>
    }

    val orderedEmployeeRecommendations: List[EmployeeRecommendation] = selectedEmployeeListComingFromSearch.values.toList
      .sortWith((employeeRecommendation1, employeeRecommendation2) => {
        if (employeeRecommendation1.upForRedeploymentDate.isEmpty && employeeRecommendation2.upForRedeploymentDate.isEmpty) false
        else if (employeeRecommendation1.upForRedeploymentDate.isEmpty && employeeRecommendation2.upForRedeploymentDate.nonEmpty) true
        else if (employeeRecommendation1.upForRedeploymentDate.nonEmpty && employeeRecommendation2.upForRedeploymentDate.isEmpty) false
        else employeeRecommendation1.upForRedeploymentDate.get.isBefore(employeeRecommendation2.upForRedeploymentDate.get)
      })

    val finalList: List[EmployeeRecommendation] = orderedEmployeeRecommendations.slice(skip, top)

    val reachedEndOfResults: Boolean = if (orderedEmployeeRecommendations.size > top) false else true

    if (orderedEmployeeRecommendations.size > top) {
      val lastUpForRedeploymentDateFromCurrent = orderedEmployeeRecommendations(top - 1).upForRedeploymentDate
      val firstRedeploymentDateFromNextPage = orderedEmployeeRecommendations(top).upForRedeploymentDate

      val availabilitySearchStartPoint =
        if (lastUpForRedeploymentDateFromCurrent.nonEmpty && firstRedeploymentDateFromNextPage.nonEmpty &&
          lastUpForRedeploymentDateFromCurrent.get.equals(firstRedeploymentDateFromNextPage.get) ||
          lastUpForRedeploymentDateFromCurrent.isEmpty && firstRedeploymentDateFromNextPage.isEmpty) {
          val emailsToBeExcludedFromNextPage: List[String] = finalList.filter(_.upForRedeploymentDate.equals(lastUpForRedeploymentDateFromCurrent)).map(_.email)
          val newAvailabilityStartLimit = lastUpForRedeploymentDateFromCurrent.map(DateTimeFormatter.ISO_INSTANT.format(_))
          if (cachedStartPoint.nonEmpty && newAvailabilityStartLimit.equals(cachedStartPoint.get.availabilityStartLimit)) {
            AvailabilitySearchStartPoint(availabilityStartLimit = newAvailabilityStartLimit, emailsToBeExcludedFromNextPage ++ cachedStartPoint.get.ignoreEmailAddresses)
          } else {
            AvailabilitySearchStartPoint(availabilityStartLimit = newAvailabilityStartLimit, emailsToBeExcludedFromNextPage)
          }
        } else {
          AvailabilitySearchStartPoint(firstRedeploymentDateFromNextPage.map(DateTimeFormatter.ISO_INSTANT.format(_)), List())
        }

      availabilityPaginationCache.put(searchQueryIdentifier.copy(skip = offset + 50), availabilitySearchStartPoint)
    }

    val optimizedRequiredSkills = RecommendationEngineUtil.expandQueryListToHighlightingCombinations(requiredSkills)
    val enhancedFinalList: List[EmployeeRecommendation] = finalList.map(p => p.enhanceHighlightTerms(optimizedRequiredSkills))
    (reachedEndOfResults, enhancedFinalList)

  }

  private def buildEmployeeRecommendationsFromEmailContentAndOrderByAvailability(searchResultAsMapFromJson: Map[String, Any],
                                                                                 stage: SearchCriterionType,
                                                                                 mailAddressesToUserProfileMap: Map[String, EmployeeRecommendation],
                                                                                 taxonomyList: List[TaxonomyType.Value] = List(TaxonomyType.SOFTWARE),
                                                                                 hierarchyOrderingScore: Double,
                                                                                 fieldName: String,
                                                                                 searchMappings: Boolean = false,
                                                                                 emailAddressesOrderByAvailability: mutable.LinkedHashSet[String] = new mutable.LinkedHashSet[String]()): List[EmployeeRecommendation] = {

    val (_, mailToEmployeeRecommendationMap: mutable.Map[String, EmployeeRecommendation]) = buildEmployeeRecommendationsFromEmailContent(
      searchResultAsMapFromJson,
      stage,
      mailAddressesToUserProfileMap,
      taxonomyList,
      hierarchyOrderingScore,
      fieldName,
      searchMappings)

    val finalOrderedEmployees: List[EmployeeRecommendation] =
      if (emailAddressesOrderByAvailability.nonEmpty) emailAddressesOrderByAvailability.flatMap(email => mailToEmployeeRecommendationMap.get(email)).toList
      else mailToEmployeeRecommendationMap.values.toList

    log.info(s"[recommendation_engine][$stage] extracted recommendations length : ${finalOrderedEmployees.length}")

    finalOrderedEmployees
  }

  private def buildEmployeeRecommendationsFromReceivedEmailsContentAndOrderByAvailability(searchResultAsMapFromJson: Map[String, Any],
                                                                                          searchMappings: Boolean = false,
                                                                                          stage: SearchCriterionType,
                                                                                          mailAddressesToUserProfileMap: Map[String, EmployeeRecommendation],
                                                                                          hierarchyOrderingScore: Double,
                                                                                          taxonomyList: List[TaxonomyType.Value] = List(TaxonomyType.SOFTWARE),
                                                                                          fieldName: String,
                                                                                          emailAddressesOrderByAvailability: mutable.LinkedHashSet[String] = new mutable.LinkedHashSet[String]()): List[EmployeeRecommendation] = {

    val (_, mailToEmployeeRecommendationMap: mutable.Map[String, EmployeeRecommendation]) = buildEmployeeRecommendationsFromReceivedEmailsContent(
      searchResultAsMapFromJson,
      searchMappings,
      stage,
      mailAddressesToUserProfileMap,
      hierarchyOrderingScore,
      taxonomyList,
      fieldName)

    val finalOrderedEmployees: List[EmployeeRecommendation] =
      if (emailAddressesOrderByAvailability.nonEmpty) emailAddressesOrderByAvailability.flatMap(email => mailToEmployeeRecommendationMap.get(email)).toList
      else mailToEmployeeRecommendationMap.values.toList

    log.info(s"[recommendation_engine][$stage] extracted recommendations length : ${finalOrderedEmployees.length}")
    finalOrderedEmployees.toList

  }


}
