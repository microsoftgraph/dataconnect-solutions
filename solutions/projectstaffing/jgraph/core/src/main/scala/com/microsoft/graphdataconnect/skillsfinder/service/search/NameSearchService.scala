/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service.search

import java.net.URLEncoder
import java.util

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.SearchCriterionType
import com.microsoft.graphdataconnect.skillsfinder.exceptions.AzureSearchIndexNotFound
import com.microsoft.graphdataconnect.skillsfinder.logging.ConditionalLogger
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.recommendation.EmployeeRecommendation
import com.microsoft.graphdataconnect.skillsfinder.models.{FilterType, TaxonomyType}
import com.microsoft.graphdataconnect.skillsfinder.service.ConfigurationService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable


@Service
class NameSearchService {

  @Value("${logging.enable.recommendation.reasoning.logs}")
  var enableRecommendationReasoningLogs: Boolean = true

  private lazy val conditionalLogger: ConditionalLogger = new ConditionalLogger(enableRecommendationReasoningLogs)

  private val log: Logger = LoggerFactory.getLogger(classOf[NameSearchService])

  @Value("${azure.search.baseUrl}")
  var azureSearchBaseUrl = "https://gdcdemosearch.search.windows.net"

  private def employeesIndexName(refreshIndexConfigCache: Boolean = false): String = {
    configurationService.getAzureSearchEmployeeIndexNameFromCache(refreshIndexConfigCache)
  }

  private def employeesIndexUrl(refreshIndexConfigCache: Boolean = false): String = s"$azureSearchBaseUrl/indexes/" + employeesIndexName(refreshIndexConfigCache)

  @Autowired
  var configurationService: ConfigurationService = _

  @Autowired
  var recommendationService: RecommendationService = _


  /**
   * Search for employees matching the provided name(s)
   *
   * @param namesToBeSearched list of keywords to search for
   * @return
   */
  def getRecommendedEmployeesByName(namesToBeSearched: List[String], searchFilterValues: Map[FilterType, List[String]] = Map.empty): List[EmployeeRecommendation] = {
    performNameSearchOnUserIndex(namesToBeSearched, searchFilterValues = searchFilterValues)
  }

  private def performNameSearchOnUserIndex(namesToBeSearched: List[String],
                                           fieldName: String = "display_name,mail",
                                           stage: SearchCriterionType = SearchCriterionType.PROFILE_NAMES,
                                           givenScore: Double = 10,
                                           searchReceivers: Boolean = false,
                                           limit: Int = 2000,
                                           taxonomyList: List[TaxonomyType.Value] = TaxonomyType.values.toList,
                                           searchFilterValues: Map[FilterType, List[String]] = Map.empty): List[EmployeeRecommendation] = {

    val startTime = System.nanoTime()

    try {
      val expandedNamesToBeSearched = RecommendationEngineUtil.expandQueryList(namesToBeSearched)

      val searchString = "&search=" + URLEncoder.encode(expandedNamesToBeSearched.mkString(" "), "utf-8") //.replace(" ", "%20")
      val searchFields = s"&searchFields=$fieldName"
      val top = "&$top=" + limit.toString
      val filterParam = RecommendationEngineUtil.buildFacetFilterParamForGetRequest(searchFilterValues)

      val search_params = filterParam + searchFields + searchString + top

      log.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage] search_string unencoded: ${expandedNamesToBeSearched.mkString(" ")}")
      log.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage] search_string encoded: $searchString")
      log.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage] searchFields : $searchFields")
      log.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage] top(limit) : $top")
      log.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage] search_params : $search_params")


      val searchResultTry: Map[String, Any] =
        try {
          val indexName = employeesIndexName()
          recommendationService.executeSearch(indexName, search_params, stage)
        } catch {
          case _: AzureSearchIndexNotFound =>
            //In case employee index name is obsolete, refresh index name cache and retry request to Azure Search
            val indexName = employeesIndexName(true)
            recommendationService.executeSearch(indexName, search_params, stage)
        }

      val retrievedSearchResults: List[EmployeeRecommendation] = searchResultTry.get("value").map {
        searchResults: Any =>
          val convertedSearchResults = searchResults.asInstanceOf[java.util.ArrayList[java.util.HashMap[String, Any]]].asScala
          log.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage] length search results : ${convertedSearchResults.length}")

          convertedSearchResults.
            map {
              nestedResult =>
                buildEmployeeRecommendationFromNameSearch(givenScore, nestedResult, taxonomyList = taxonomyList)
            }.toList
      }.getOrElse(Nil)

      val selectedUsers = new scala.collection.mutable.HashSet[String]()
      val finalEmployees = new scala.collection.mutable.ListBuffer[EmployeeRecommendation]()


      retrievedSearchResults.foreach {
        scoredEmployee =>
          if (!selectedUsers.contains(scoredEmployee.email.toLowerCase())) {
            val reasoningExplanation = s"[recommendation_engine][performNameSearchOnUserIndex][reasoning] Employee: ${scoredEmployee.email} was selected in stage: $stage because the following terms:[ ${scoredEmployee.highlightedTerms.mkString(",")} ] were found in the following fields [$fieldName]"
            conditionalLogger.info(reasoningExplanation)
            finalEmployees += scoredEmployee
            selectedUsers += scoredEmployee.email.toLowerCase
            conditionalLogger.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage] result: $scoredEmployee")
          }
      }

      log.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage] recommendation list length : ${finalEmployees.toList.length}")

      val selectedEmployeeListComingFromSearch: mutable.LinkedHashMap[String, EmployeeRecommendation] = mutable.LinkedHashMap(finalEmployees.toList.map(p => (p.email -> p)): _*)

      selectedEmployeeListComingFromSearch.values.toList
    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000
      log.info(s"[recommendation_engine][performNameSearchOnUserIndex][$stage][total_time]: $diffTime")
    }
  }

  private def buildEmployeeRecommendationFromNameSearch(givenScore: Double,
                                                        userProfileSearchRecord: util.HashMap[String, Any],
                                                        requiredSkills: List[String] = List.empty,
                                                        computeInferredSkills: Boolean = true,
                                                        taxonomyList: List[TaxonomyType.Value] = List.empty): EmployeeRecommendation = {
    val nestedMapResult = userProfileSearchRecord.asScala //asInstanceOf[Map[String,Any]]

    val employeeMail = nestedMapResult("mail").toString

    val employeeSkills = nestedMapResult("skills").toString.split(",").toList.map(_.toLowerCase.trim)

    val employeeSkillsInferredFromDomainExpert: Map[String, List[String]] = taxonomyList.map {
      taxoName =>
        val deFieldName = "de_" + taxoName
        (taxoName.toString -> nestedMapResult(deFieldName).toString.split(",").toList)
    }.toMap

    EmployeeRecommendation(
      employeeMail,
      givenScore,
      employeeSkills,
      employeeSkills,
      List.empty[String],
      employeeSkillsInferredFromDomainExpert)
  }

}






