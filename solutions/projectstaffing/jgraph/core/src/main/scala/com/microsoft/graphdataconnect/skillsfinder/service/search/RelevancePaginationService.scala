package com.microsoft.graphdataconnect.skillsfinder.service.search

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.{SearchCriterion, SearchCriterionType}
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.recommendation.EmployeeRecommendation
import com.microsoft.graphdataconnect.skillsfinder.models.{FilterType, SearchSettingsRequestResponse, SearchWeightingOrder, TaxonomyType}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

@Service
class RelevancePaginationService extends PaginationService {

  private val log: Logger = LoggerFactory.getLogger(classOf[RelevancePaginationService])

  @Value("${azure.search.emailsIndex}")
  var emailsIndexName = "gdc-mails-20201014"

  /**
   * M365 Profile skills
   * M365 Profile About Me
   * M365 Profile Topics
   * Email content - exact match
   * Email - content - lemmas
   * DE expansion M365 Profile skills
   * DE expansion M365 About Me
   * DE expansion Profile Topics
   * DE expansion Email content
   *
   * @param requiredSkills list of keywords to search for
   * @return
   */

  def getRecommendedEmployees(requiredSkills: List[String],
                              searchSettings: SearchSettingsRequestResponse,
                              searchFilterValues: Map[FilterType, List[String]] = Map.empty,
                              size: Int,
                              offset: Int,
                              availableAtTheLatestOn: String): (Boolean, List[EmployeeRecommendation]) = {

    var reachedEndOfResults: Boolean = true

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

    var top = size
    var skip = offset

    val employeeEmailAddressesToBeIgnored: mutable.Set[String] = mutable.Set()

    def updateSearchLimits(totalCount: Int): Unit = {
      if (top != 0) {
        if (selectedEmployeeListComingFromSearch.size != size) {
          skip = skip - totalCount
          top = if (skip < 0) top + skip else top
          skip = if (skip < 0) 0 else skip
        } else if (selectedEmployeeListComingFromSearch.size == size) {
          skip = 0
          top = 0
        }
      }
    }

    var requestedEmployeeAddresses: mutable.Set[String] = mutable.Set()

    def searchForProfileCriterion(searchCriterion: SearchCriterionType, fieldName: String, searchMappings: Boolean = false): Unit = {
      if (requestedEmployeeAddresses.nonEmpty) {
        val (_, m365UsersSkills: List[EmployeeRecommendation]) = performSearchOnUserIndex(requiredSkills,
          fieldName = fieldName,
          stage = searchCriterion,
          givenScore = hierarchyOrderScore,
          skip = 0,
          top = requestedEmployeeAddresses.size,
          taxonomyList = taxonomyList,
          searchMappings = searchMappings,
          searchFilterValues = searchFilterValues,
          employeeAddressesToBeIgnored = Set(),
          requestedEmployeeAddresses = requestedEmployeeAddresses.toSet)

        selectedEmployeeListComingFromSearch = updateEmployeeRecommendationWithNewInformation(selectedEmployeeListComingFromSearch, m365UsersSkills)
      }

      if (top != 0) {
        val (emailAddressFacet: Set[String], m365UsersSkills: List[EmployeeRecommendation]) = performSearchOnUserIndex(requiredSkills,
          fieldName = fieldName,
          stage = searchCriterion,
          givenScore = hierarchyOrderScore,
          skip = skip,
          top = top,
          taxonomyList = taxonomyList,
          searchMappings = searchMappings,
          searchFilterValues = searchFilterValues,
          employeeAddressesToBeIgnored = employeeEmailAddressesToBeIgnored.toSet,
          requestedEmployeeAddresses = Set(),
          availableAtTheLatestOn = Some(availableAtTheLatestOn))

        selectedEmployeeListComingFromSearch = selectedEmployeeListComingFromSearch ++ m365UsersSkills.map(employee => employee.mailAddressFormatted() -> employee)

        val totalNumOfEmployeesThatMatchQuery =
          if (m365UsersSkills.size != top) {
            employeeEmailAddressesToBeIgnored ++= emailAddressFacet
            emailAddressFacet.size
          } else {
            m365UsersSkills.size
          }

        if (m365UsersSkills.size != top) {
          reachedEndOfResults = true
        } else {
          if (emailAddressFacet.size > skip + top) {
            reachedEndOfResults = false
          } else {
            reachedEndOfResults = true
          }
        }

        requestedEmployeeAddresses ++= m365UsersSkills.map(_.email).toSet

        updateSearchLimits(totalNumOfEmployeesThatMatchQuery)

        hierarchyOrderScore -= 1
      }
    }

    val isEmailContentSearchCriteriaActivated = searchSettings.searchCriteria
      .exists((searchCriterion: SearchCriterion) => searchCriterion.searchCriterionType.equals(SearchCriterionType.EMAIL_CONTENT) && searchCriterion.isActive)

    val (facets: Set[String], employeesThatMatchFilter: List[EmployeeRecommendation]) = performSearchOnUserIndex(requiredSkills = List(),
      fieldName = "",
      searchFilterValues = searchFilterValues,
      availableAtTheLatestOn = Some(availableAtTheLatestOn))

    val mailToUserProfile: Map[String, EmployeeRecommendation] = employeesThatMatchFilter
      .map(employeeRecommendation => (employeeRecommendation.email, employeeRecommendation)).toMap

    var emailAddressesOfEmployeesThatMatchFilter: Set[String] = if (isEmailContentSearchCriteriaActivated) {
      facets
    } else Set()

    def searchForEmailContentCriterion(searchCriterion: SearchCriterionType, fieldName: String): Unit = {
      if (requestedEmployeeAddresses.nonEmpty) {

        val searchResultAsMapFromJson: Map[String, Any] = performSearchOnSendMails(requiredSkills,
          indexName = emailsIndexName,
          fieldName = fieldName,
          stage = searchCriterion,
          searchSettings = searchSettings,
          requestedEmployeeAddresses = requestedEmployeeAddresses.toSet
        )

        val (_, mailContentRecommendations: List[EmployeeRecommendation]) = buildEmployeeRecommendationsAndOrderByRelevance(searchResultAsMapFromJson = searchResultAsMapFromJson,
          searchSettings = searchSettings,
          stage = searchCriterion,
          mailAddressesToUserProfileMap = mailToUserProfile,
          taxonomyList = taxonomyList,
          hierarchyOrderingScore = hierarchyOrderScore,
          fieldName = fieldName
        )

        selectedEmployeeListComingFromSearch = updateEmployeeRecommendationWithNewInformation(selectedEmployeeListComingFromSearch, mailContentRecommendations)
      }

      if (top != 0) {

        emailAddressesOfEmployeesThatMatchFilter = emailAddressesOfEmployeesThatMatchFilter.diff(employeeEmailAddressesToBeIgnored)

        if (emailAddressesOfEmployeesThatMatchFilter.nonEmpty) {
          val searchResultAsMapFromJson: Map[String, Any] = performSearchOnSendMails(requiredSkills,
            indexName = emailsIndexName,
            fieldName = fieldName,
            stage = searchCriterion,
            searchSettings = searchSettings,
            requestedEmployeeAddresses = emailAddressesOfEmployeesThatMatchFilter
          )

          var (emailAddressFacet: Set[String], mailContentRecommendations: List[EmployeeRecommendation]) = buildEmployeeRecommendationsAndOrderByRelevance(searchResultAsMapFromJson = searchResultAsMapFromJson,
            searchSettings = searchSettings,
            stage = searchCriterion,
            mailAddressesToUserProfileMap = mailToUserProfile,
            taxonomyList = taxonomyList,
            hierarchyOrderingScore = hierarchyOrderScore,
            fieldName = fieldName
          )

          if (mailContentRecommendations.size > skip + top) {
            reachedEndOfResults = false
          } else {
            reachedEndOfResults = true
          }
          mailContentRecommendations = mailContentRecommendations.slice(skip, skip + top)

          selectedEmployeeListComingFromSearch = selectedEmployeeListComingFromSearch ++ mailContentRecommendations.map(employee => employee.mailAddressFormatted() -> employee)

          val totalNumOfEmployeesThatMatchQuery =
            if (mailContentRecommendations.size != top) {
              employeeEmailAddressesToBeIgnored ++= emailAddressFacet
              emailAddressFacet.size
            } else {
              mailContentRecommendations.size
            }

          requestedEmployeeAddresses ++= mailContentRecommendations.map(_.email).toSet

          updateSearchLimits(totalNumOfEmployeesThatMatchQuery)

          hierarchyOrderScore -= 1
        }

      }
    }

    def searchForReceivedEmailContentCriterion(searchCriterionType: SearchCriterionType, fieldName: String): Unit = {

      var emailAddressFacet: Set[String] = Set()
      var mailContentRecommendationsReceiver: List[EmployeeRecommendation] = List()

      if (requestedEmployeeAddresses.nonEmpty) {
        val searchResultAsMapFromJson = performSearchOnReceivedMails(requiredSkills,
          indexName = emailsIndexName,
          fieldName = fieldName,
          stage = searchCriterionType,
          searchSettings = searchSettings)

        buildEmployeeRecommendationsFromReceivedEmailsContentAndOrderByRelevance(searchResultAsMapFromJson = searchResultAsMapFromJson,
          searchSettings = searchSettings,
          stage = searchCriterionType,
          mailAddressesToUserProfileMap = mailToUserProfile,
          hierarchyOrderingScore = hierarchyOrderScore,
          taxonomyList = taxonomyList,
          fieldName = fieldName) match {
          case (facets, recommendations) =>
            emailAddressFacet = facets
            mailContentRecommendationsReceiver = recommendations
        }

        val augmentationInfoForRequestedEmployees = mailContentRecommendationsReceiver.filter(employeeRecommendation => requestedEmployeeAddresses.contains(employeeRecommendation.email))

        selectedEmployeeListComingFromSearch = updateEmployeeRecommendationWithNewInformation(selectedEmployeeListComingFromSearch, augmentationInfoForRequestedEmployees)
      }

      if (top != 0) {

        emailAddressesOfEmployeesThatMatchFilter = emailAddressesOfEmployeesThatMatchFilter.diff(employeeEmailAddressesToBeIgnored)
        if (emailAddressesOfEmployeesThatMatchFilter.nonEmpty) {
          if (emailAddressFacet.isEmpty && mailContentRecommendationsReceiver.isEmpty) {
            val searchResultAsMapFromJson = performSearchOnReceivedMails(requiredSkills,
              indexName = emailsIndexName,
              fieldName = fieldName,
              stage = searchCriterionType,
              searchSettings = searchSettings)

            buildEmployeeRecommendationsFromReceivedEmailsContentAndOrderByRelevance(searchResultAsMapFromJson = searchResultAsMapFromJson,
              searchSettings = searchSettings,
              stage = searchCriterionType,
              mailAddressesToUserProfileMap = mailToUserProfile,
              hierarchyOrderingScore = hierarchyOrderScore,
              taxonomyList = taxonomyList,
              fieldName = fieldName) match {
              case (facets, recommendations) =>
                emailAddressFacet = facets
                mailContentRecommendationsReceiver = recommendations
            }
          }

          val employeeRecommendationsThatMatchTheFilter = mailContentRecommendationsReceiver
            .filter(employeeRecommendation => emailAddressesOfEmployeesThatMatchFilter.contains(employeeRecommendation.email))

          mailContentRecommendationsReceiver = employeeRecommendationsThatMatchTheFilter.slice(skip, skip + top)
          if (employeeRecommendationsThatMatchTheFilter.size > skip + top) {
            reachedEndOfResults = false
          } else {
            reachedEndOfResults = true
          }

          selectedEmployeeListComingFromSearch = selectedEmployeeListComingFromSearch ++ mailContentRecommendationsReceiver.map(employee => employee.mailAddressFormatted() -> employee)

          val totalNumOfEmployeesThatMatchQuery =
            if (mailContentRecommendationsReceiver.size != top) {
              employeeEmailAddressesToBeIgnored ++= emailAddressFacet
              employeeRecommendationsThatMatchTheFilter.size
            } else {
              mailContentRecommendationsReceiver.size
            }

          requestedEmployeeAddresses ++= mailContentRecommendationsReceiver.map(_.email).toSet

          updateSearchLimits(totalNumOfEmployeesThatMatchQuery)

          hierarchyOrderScore -= 1
        }
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

        searchForProfileCriterion(SearchCriterionType.DE_PROFILE, taxoFieldLists, searchMappings = true)

      case SearchCriterionType.DE_EMAIL_CONTENT =>

        searchForEmailContentCriterion(SearchCriterionType.DE_EMAIL_CONTENT, taxoFieldLists)

      case SearchCriterionType.RECEIVED_EMAIL_CONTENT =>

        searchForReceivedEmailContentCriterion(SearchCriterionType.RECEIVED_EMAIL_CONTENT, "Content,Content_v2,lemma_body")

      case SearchCriterionType.DE_RECEIVED_EMAIL_CONTENT =>

        searchForReceivedEmailContentCriterion(SearchCriterionType.DE_RECEIVED_EMAIL_CONTENT, taxoFieldLists)

      case _ =>
    }

    val finalList = selectedEmployeeListComingFromSearch.toList.map(_._2)

    val optimizedRequiredSkills = RecommendationEngineUtil.expandQueryListToHighlightingCombinations(requiredSkills)
    val enhancedFinalList = finalList.map(p => p.enhanceHighlightTerms(optimizedRequiredSkills))
    if (enhancedFinalList.size < size) {
      reachedEndOfResults = true
    }
    (reachedEndOfResults, enhancedFinalList)

  }


  private def buildEmployeeRecommendationsAndOrderByRelevance(searchResultAsMapFromJson: Map[String, Any],
                                                              searchSettings: SearchSettingsRequestResponse,
                                                              stage: SearchCriterionType,
                                                              mailAddressesToUserProfileMap: Map[String, EmployeeRecommendation],
                                                              taxonomyList: List[TaxonomyType.Value] = List(TaxonomyType.SOFTWARE),
                                                              hierarchyOrderingScore: Double,
                                                              fieldName: String,
                                                              searchMappings: Boolean = false): (Set[String], List[EmployeeRecommendation]) = {

    val (emailFacets: mutable.LinkedHashSet[String], mailToEmployeeRecommendationMap: mutable.Map[String, EmployeeRecommendation]) = buildEmployeeRecommendationsFromEmailContent(
      searchResultAsMapFromJson,
      stage,
      mailAddressesToUserProfileMap,
      taxonomyList,
      hierarchyOrderingScore,
      fieldName,
      searchMappings)

    val (searchRelevanceWeight: Double, searchFreshnessWeight: Double, searchVolumeWeight: Double) = RecommendationEngineUtil.extractSearchCriteriaWeight(searchSettings)

    val orderedEmployeesByVolume: mutable.LinkedHashSet[String] = if (searchVolumeWeight != 0) {
      mutable.LinkedHashSet[String](emailFacets.toSeq: _*)
    }
    else {
      mutable.LinkedHashSet.empty[String]
    }

    val emailAddressFacet: Set[String] = orderedEmployeesByVolume.toSet

    //Observation: we use sorted map because we want to have fast removals
    //We sort the search results list by the date
    var orderedEmployeesByDateFreshness: mutable.LinkedHashSet[String] =
    if (searchFreshnessWeight != 0) {
      val orderedResults: Seq[String] = mailToEmployeeRecommendationMap.values.toSeq
        .map(x => (x.email.trim.toLowerCase, x.recSourceDate))
        .sortBy(mailWithSourceDate => mailWithSourceDate._2)
        .reverse
        .map(mailWithSourceDate => mailWithSourceDate._1)
        .filter {
          employeeEmailAddress =>
            mailAddressesToUserProfileMap.contains(employeeEmailAddress)
        }
      mutable.LinkedHashSet(orderedResults: _*)
    }
    else {
      mutable.LinkedHashSet.empty[String]
    }

    //here we dont need sorting because the search results are coming ordered by score
    var orderedEmployeesByScore: mutable.LinkedHashSet[String] = {
      val orderedResults: Seq[String] = mailToEmployeeRecommendationMap.values.toSeq
        .map(x => x.email.trim.toLowerCase)
        .filter(employeeEmailAddress => mailAddressesToUserProfileMap.contains(employeeEmailAddress))
        .distinct

      mutable.LinkedHashSet(orderedResults: _*)
    }

    val finalOrderedEmployees: ListBuffer[EmployeeRecommendation] = orderEmployeesBySearchRankingCriteria(searchRelevanceWeight,
      searchFreshnessWeight,
      searchVolumeWeight,
      mailToEmployeeRecommendationMap,
      orderedEmployeesByScore,
      orderedEmployeesByVolume,
      orderedEmployeesByDateFreshness,
      stage,
      fieldName)

    log.info(s"[recommendation_engine][$stage] extracted recommendations length : ${finalOrderedEmployees.toList.length}")

    //TODO the response coming from Az Search might be bigger than 1 page (we will have to make more than 1 request to the email index for EMAIL_CONTENT/DE_EMAIL_CONTENT criterion)
    // then we have to use emailAddressFacet instead of emailAddressesOfEmployees
    val emailAddressesOfEmployees = finalOrderedEmployees.map(_.email).toSet

    (emailAddressesOfEmployees, finalOrderedEmployees.toList)
  }

  private def buildEmployeeRecommendationsFromReceivedEmailsContentAndOrderByRelevance(searchResultAsMapFromJson: Map[String, Any],
                                                                                       searchMappings: Boolean = false,
                                                                                       searchSettings: SearchSettingsRequestResponse,
                                                                                       stage: SearchCriterionType,
                                                                                       mailAddressesToUserProfileMap: Map[String, EmployeeRecommendation],
                                                                                       hierarchyOrderingScore: Double,
                                                                                       taxonomyList: List[TaxonomyType.Value] = List(TaxonomyType.SOFTWARE),
                                                                                       fieldName: String): (Set[String], List[EmployeeRecommendation]) = {

    val (searchRelevanceWeight: Double, searchFreshnessWeight: Double, searchVolumeWeight: Double) = RecommendationEngineUtil.extractSearchCriteriaWeight(searchSettings)

    val (emailAddressFacet: mutable.LinkedHashSet[String], mailToEmployeeRecommendationMap: mutable.Map[String, EmployeeRecommendation]) = buildEmployeeRecommendationsFromReceivedEmailsContent(
      searchResultAsMapFromJson,
      searchMappings,
      stage,
      mailAddressesToUserProfileMap,
      hierarchyOrderingScore,
      taxonomyList,
      fieldName)

    val orderedEmployeesByVolume: mutable.LinkedHashSet[String] = if (searchVolumeWeight != 0) {
      mutable.LinkedHashSet[String](emailAddressFacet.toSeq: _*)
    } else {
      mutable.LinkedHashSet.empty[String]
    }


    //Observation: we use sorted map because we want to have fast removals
    //We sort the search results list by the date
    var orderedEmployeesByDateFreshness: mutable.LinkedHashSet[String] = if (searchFreshnessWeight != 0) {
      val orderedResults = mailToEmployeeRecommendationMap.values.toList
        .map((x: EmployeeRecommendation) => (x.email.trim.toLowerCase, x.recSourceDate))
        .sortBy(mailWithSourceDate => mailWithSourceDate._2)
        .reverse
        .map(mailWithSourceDate => mailWithSourceDate._1)
        .filter {
          employeeEmailAddress =>
            mailAddressesToUserProfileMap.contains(employeeEmailAddress)
        }
      mutable.LinkedHashSet(orderedResults: _*)
    }
    else {
      mutable.LinkedHashSet.empty[String]
    }


    //here we dont need sorting because the search results are coming ordered by score
    var orderedEmployeesByScore: mutable.LinkedHashSet[String] = {
      val orderedResults = mailToEmployeeRecommendationMap.values.toList.map(x => x.email.trim.toLowerCase).filter {
        employeeEmailAddress =>
          mailAddressesToUserProfileMap.contains(employeeEmailAddress)
      }.distinct

      mutable.LinkedHashSet(orderedResults: _*)
    }

    val finalOrderedEmployees: ListBuffer[EmployeeRecommendation] = orderEmployeesBySearchRankingCriteria(searchRelevanceWeight,
      searchFreshnessWeight,
      searchVolumeWeight,
      mailToEmployeeRecommendationMap,
      orderedEmployeesByScore,
      orderedEmployeesByVolume,
      orderedEmployeesByDateFreshness,
      stage,
      fieldName)

    //TODO the response coming from Az Search might be bigger than 1 page (we will have to make more than 1 request to the email index for EMAIL_CONTENT/DE_EMAIL_CONTENT criterion)
    // then we have to use emailAddressFacet instead of emailAddressesOfEmployees
    val emailAddressesOfEmployees = finalOrderedEmployees.map(_.email).toSet

    log.info(s"[recommendation_engine][$stage] extracted recommendations length : ${finalOrderedEmployees.toList.length}")
    (emailAddressesOfEmployees.toSet, finalOrderedEmployees.toList)
  }

  private def orderEmployeesBySearchRankingCriteria(searchRelevanceWeight: Double,
                                                    searchFreshnessWeight: Double,
                                                    searchVolumeWeight: Double,
                                                    mailToEmployeeRecommendationMap: mutable.Map[String, EmployeeRecommendation],
                                                    orderedEmployeesByScore: mutable.LinkedHashSet[String],
                                                    orderedEmployeesByVolume: mutable.LinkedHashSet[String],
                                                    orderedEmployeesByDateFreshness: mutable.LinkedHashSet[String],
                                                    stage: SearchCriterionType,
                                                    fieldName: String) = {
    val orderProcessingList: Seq[(SearchWeightingOrder.Value, Int)] = RecommendationEngineUtil.getOrderProcessingList(searchRelevanceWeight, searchFreshnessWeight, searchVolumeWeight, mailToEmployeeRecommendationMap.size)

    val finalOrderedEmployees = new scala.collection.mutable.ListBuffer[EmployeeRecommendation]()
    val selectedUsers = new scala.collection.mutable.HashSet[String]()


    var relevanceCounter = 0
    var volumeCounter = 0
    var freshnessCounter = 0

    orderProcessingList.toList.foreach {
      case (criteria, lengthOfList) =>
        criteria match {
          case SearchWeightingOrder.RELEVANCE =>
            val orderedEmployeesEmailAddresses: Iterable[String] = orderedEmployeesByScore.toList
            orderedEmployeesEmailAddresses.toList.foreach {
              emailAddress =>
                if (mailToEmployeeRecommendationMap.contains(emailAddress) && !selectedUsers.contains(emailAddress) && relevanceCounter <= lengthOfList) {

                  selectedUsers += emailAddress
                  val scoredEmployee = mailToEmployeeRecommendationMap(emailAddress)
                  finalOrderedEmployees += scoredEmployee
                  val reasoningExplanation = s"[recommendation_engine][reasoning] Employee: ${scoredEmployee.email} was selected in stage: $stage based on [relevance_criteria] because the following terms:[ ${scoredEmployee.highlightedTerms.mkString(",")} ] were found in the following fields [$fieldName]"
                  conditionalLogger.info(reasoningExplanation)
                  orderedEmployeesByVolume -= emailAddress
                  orderedEmployeesByDateFreshness -= emailAddress
                  relevanceCounter += 1
                }
            }
          case SearchWeightingOrder.VOLUME =>
            val orderedEmployeesEmailAddresses: Iterable[String] = orderedEmployeesByVolume.toList
            orderedEmployeesEmailAddresses.toList.foreach {
              emailAddress =>
                if (mailToEmployeeRecommendationMap.contains(emailAddress) && !selectedUsers.contains(emailAddress) && volumeCounter <= lengthOfList) {
                  selectedUsers += emailAddress
                  val scoredEmployee = mailToEmployeeRecommendationMap(emailAddress)
                  finalOrderedEmployees += scoredEmployee
                  val reasoningExplanation = s"[recommendation_engine][reasoning] Employee: ${scoredEmployee.email} was selected in stage: $stage based on [volume_criteria] because the following terms:[ ${scoredEmployee.highlightedTerms.mkString(",")} ] were found in the following fields [$fieldName]"
                  conditionalLogger.info(reasoningExplanation)
                  orderedEmployeesByScore -= emailAddress
                  orderedEmployeesByDateFreshness -= emailAddress
                  volumeCounter += 1
                }
            }
          case SearchWeightingOrder.FRESHNESS =>
            val orderedEmployeesEmailAddresses: Iterable[String] = orderedEmployeesByDateFreshness.toList
            orderedEmployeesEmailAddresses.toList.foreach {
              emailAddress =>
                if (mailToEmployeeRecommendationMap.contains(emailAddress) && !selectedUsers.contains(emailAddress) && freshnessCounter <= lengthOfList) {
                  selectedUsers += emailAddress
                  val scoredEmployee = mailToEmployeeRecommendationMap(emailAddress)
                  finalOrderedEmployees += scoredEmployee
                  val reasoningExplanation = s"[recommendation_engine][reasoning] Employee: ${scoredEmployee.email} was selected in stage: $stage based on [freshness_criteria] because the following terms:[ ${scoredEmployee.highlightedTerms.mkString(",")} ] were found in the following fields [$fieldName]"
                  conditionalLogger.info(reasoningExplanation)

                  orderedEmployeesByVolume -= emailAddress
                  orderedEmployeesByScore -= emailAddress
                  freshnessCounter += 1
                }
            }
        }
    }
    finalOrderedEmployees
  }

}
