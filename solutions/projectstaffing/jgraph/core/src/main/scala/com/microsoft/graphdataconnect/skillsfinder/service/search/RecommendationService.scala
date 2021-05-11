/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service.search

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.{HttpResponse, Unirest}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.SearchCriterionType
import com.microsoft.graphdataconnect.skillsfinder.exceptions.{AzureSearchIndexNotFound, ServerErrorException}
import com.microsoft.graphdataconnect.skillsfinder.logging.ConditionalLogger
import com.microsoft.graphdataconnect.skillsfinder.models.SearchResultsFilter.SearchResultsFilter
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.recommendation.EmployeeRecommendation
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.{EmployeeDetails, recommendation}
import com.microsoft.graphdataconnect.skillsfinder.models.{FilterType, SearchFilterValues, SearchResultsFilter, TaxonomyType}
import com.microsoft.graphdataconnect.skillsfinder.service.ConfigurationService
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpStatus
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.{immutable, mutable}
import scala.util.{Failure, Success, Try}


@Service
class RecommendationService {

  @Value("${logging.enable.recommendation.reasoning.logs}")
  var enableRecommendationReasoningLogs: Boolean = true

  private lazy val conditionalLogger: ConditionalLogger = new ConditionalLogger(enableRecommendationReasoningLogs)

  private val log: Logger = LoggerFactory.getLogger(classOf[RecommendationService])

  @Value("${azure.search.baseUrl}")
  var azureSearchBaseUrl = "https://gdcdemosearch.search.windows.net"

  @Value("${gdc-search-service-key:#{environment.AZURE_SEARCH_APIKEY}}")
  var azureSearchApiKey: String = _

  private lazy val headers = Map("Content-Type" -> "application/json", "api-key" -> azureSearchApiKey)

  private val api_version = "?api-version=2020-06-30"

  @Autowired
  var configurationService: ConfigurationService = _

  private def employeesIndexName(refreshIndexConfigCache: Boolean = false): String = {
    configurationService.getAzureSearchEmployeeIndexNameFromCache(refreshIndexConfigCache)
  }

  private def employeesIndexUrl(refreshIndexConfigCache: Boolean = false): String = s"$azureSearchBaseUrl/indexes/" + employeesIndexName(refreshIndexConfigCache)


  def executeSearch(indexName: String, search_params: String, stage: SearchCriterionType): Map[String, Any] = {

    val startTime = System.nanoTime()
    try {
      val searchUrl = this.azureSearchBaseUrl + "/indexes/" + indexName + "/docs" + this.api_version + search_params
      log.info(s"[recommendation_engine][$stage] url : $searchUrl")

      Try {
        Unirest.get(searchUrl).headers(this.headers.asJava).asString()
      } match {
        case Success(response: HttpResponse[String]) =>
          if (response.getStatus == HttpStatus.SC_OK) {
            val jsonResponse = response.getBody
            val mapper = new ObjectMapper()
            val res: Map[String, Any] = mapper.readValue(jsonResponse, classOf[java.util.HashMap[String, Any]]).asScala.toMap
            res
          }
          else {
            val errorMessage = Try(response.getBody).getOrElse("") + " " + Option(response.getStatusText).getOrElse("") + " http_response_code: " + Option(response.getStatus).getOrElse(0).toString
            if (response.getStatus == HttpStatus.SC_NOT_FOUND) {
              throw new AzureSearchIndexNotFound(indexName)
            }
            throw new ServerErrorException(s"[recommendation_engine][exception][$stage]Search request failed for $search_params. Error message: $errorMessage")
          }

        case Failure(exception) =>
          throw new ServerErrorException(s"[recommendation_engine][exception][$stage]Search request failed for $search_params. Error message: ${exception.getMessage}")
      }

    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000000
      log.info(s"[recommendation_engine][execute_search][$stage][total_time]: $diffTime")
    }
  }


  def explainEmployeeRecommendation(scoredEmployee: EmployeeRecommendation,
                                    searchMappings: Boolean = false,
                                    fieldName: String,
                                    stage: SearchCriterionType): Unit = {
    val reasoningExplanation = if (searchMappings) {
      s"[recommendation_engine][reasoning] Employee: ${scoredEmployee.email} was selected in stage: " +
        s"$stage because the following terms:[ ${scoredEmployee.highlightedTerms.mkString(",")} ] " +
        s"were found in the following fields [${scoredEmployee.foundReferenceField}]. " +
        s"Found content:[ ${scoredEmployee.foundReferenceText} ] "
    } else {
      s"[recommendation_engine][reasoning] Employee: ${scoredEmployee.email} was selected in stage: " +
        s"$stage because the following terms:[ ${scoredEmployee.highlightedTerms.mkString(",")} ] " +
        s"were found in the following fields [$fieldName]"
    }
    conditionalLogger.info(reasoningExplanation)
  }

  def returnRelatedTermsForTerm(mappingTermToRelatedTerms: Map[String, mutable.LinkedHashSet[String]], termsInQuery: List[String]): List[String] = {
    val collectedRelatedTerms = mutable.ListBuffer[String]()
    termsInQuery.foreach {
      highlightedTerm =>
        if (mappingTermToRelatedTerms.contains(highlightedTerm)) {
          collectedRelatedTerms ++= mappingTermToRelatedTerms(highlightedTerm).toList
        }
        else {
          mappingTermToRelatedTerms.foreach {
            case (key: String, associatedTerms: mutable.LinkedHashSet[String]) =>
              val allRelatedTerms = List(key) ++ associatedTerms.toList

              val markedLine = allRelatedTerms.map {
                relatedTerm =>
                  if (StringUtils.equalsIgnoreCase(highlightedTerm, relatedTerm)
                    || relatedTerm.startsWith(highlightedTerm + " ")
                    || relatedTerm.startsWith(highlightedTerm + "-")
                    || relatedTerm.startsWith(highlightedTerm + ".")
                    || relatedTerm.endsWith(" " + highlightedTerm)
                    || relatedTerm.endsWith("-" + highlightedTerm)
                    || relatedTerm.endsWith("." + highlightedTerm)
                    || relatedTerm.contains("." + highlightedTerm + ".")
                    || relatedTerm.contains(" " + highlightedTerm + " ")
                    || relatedTerm.contains("-" + highlightedTerm + "-")) {
                    (relatedTerm, 1)
                  }
                  else {
                    (relatedTerm, -1)
                  }
              }
              val isRelatable = markedLine.exists(p => p._2 == 1)
              if (isRelatable) {
                //val relatableTerms = markedLine.filter(p => p._2 == -1).map(p => p._1).toList
                val relatableTerms = markedLine.map(p => p._1)
                collectedRelatedTerms ++= relatableTerms
              }

          }
        }
    }

    val finalRelatedTerms = mutable.LinkedHashSet(collectedRelatedTerms.toList: _*).toList
    finalRelatedTerms
  }

  /**
   * Builds a list of *related* terms for the given search terms that were highlighted
   * highlighted means they were found in the given data source
   *
   * @param highlightedSearchTermsFinalList
   * @param mappingList
   * @return
   */
  def buildPreciseInferredSkills(highlightedSearchTermsFinalList: List[String],
                                 mappingList: String): List[String] = {
    val mappingTermToRelatedTerms: Map[String, mutable.LinkedHashSet[String]] = mappingList.split(";").map {
      line =>
        val tokens = line.split("[:]")
        val key = tokens.head
        val values = mutable.LinkedHashSet(tokens.tail.toList.mkString(":").split("[,]").toSeq: _*)
        (key, values)
    }.toMap

    returnRelatedTermsForTerm(mappingTermToRelatedTerms, highlightedSearchTermsFinalList)
  }

  def buildEmployeeRecommendationFromMailSearch(mailToUserProfile: Map[String, EmployeeRecommendation],
                                                givenScore: Double,
                                                mailSearchRecord: util.HashMap[String, Any],
                                                taxonomyList: List[TaxonomyType.Value],
                                                searchMappings: Boolean = false): Seq[EmployeeRecommendation] = {

    val nestedMapResult = mailSearchRecord.asScala //asInstanceOf[Map[String,Any]]

    val mail = nestedMapResult("TargetUser").toString.trim.toLowerCase

    val employeeSkillsInferredFromDomainExpert: Map[TaxonomyType.Value, List[String]] = taxonomyList.map {
      taxoName =>
        val deFieldName = "de_" + taxoName
        (taxoName -> nestedMapResult(deFieldName).toString.split(",").toList)
    }.toMap

    val mappingListAsMap: Map[TaxonomyType.Value, String] = taxonomyList.map {
      taxoName =>
        val deFieldName = "de_" + taxoName.toString + "_mapping"
        (taxoName -> nestedMapResult(deFieldName).toString)
    }.toMap

    val employeeHighlights: mutable.Map[String, Any] = nestedMapResult
      .getOrElse("@search.highlights", new java.util.LinkedHashMap[String, Any]())
      .asInstanceOf[util.LinkedHashMap[String, Any]].asScala

    val fieldToHighlightedTerms: mutable.Iterable[(String, String, immutable.Seq[String])] = employeeHighlights.map {
      case (fieldName, fieldBody) =>
        val strFieldBody = fieldBody.toString
        val extractedHighlightedTerms: immutable.Seq[String] = StringUtils.substringsBetween(strFieldBody, "<em>", "</em>").toList.map(_.toLowerCase)
        (fieldName, strFieldBody, extractedHighlightedTerms)
    }

    /*
    val extractedFoundText = if (searchMappings) {
      fieldToHighlightedTerms.filter(p => p._1.toString.startsWith("de_")).map { p => p._2.toString }.toList.mkString(",")
    } else {
      ""
    }
    val extractedFoundField = if (searchMappings) {
      fieldToHighlightedTerms.filter(p => p._1.toString.startsWith("de_")).map { p => p._1 }.mkString(",")
    } else {
      ""
    }
    */

    val highLightedTermsAsList = fieldToHighlightedTerms.flatMap(x => x._3).toList.distinct

    val finalHighlightedTerms = mutable.LinkedHashSet(highLightedTermsAsList ++ highLightedTermsAsList.sliding(2).map(_.mkString(" ")): _*).toList.distinct.filter(term => RecommendationEngineUtil.stopWordsSets.contains(term) == false)

    val preciseInferredSkillsMap = mappingListAsMap.map {
      case (taxo: TaxonomyType.Value, mappingListAsString: String) =>
        val preciselyInferredSkills = buildPreciseInferredSkills(finalHighlightedTerms, mappingListAsString)
        (taxo.toString -> preciselyInferredSkills)
    }

    val extractedFoundText = if (searchMappings) {
      preciseInferredSkillsMap.filter(p => p._2.nonEmpty).map { p => p._1 + ":" + p._2.mkString(",") }.toList.mkString(";")
    } else {
      ""
    }
    val extractedFoundField = if (searchMappings) {
      preciseInferredSkillsMap.filter(p => p._2.nonEmpty).map { p => p._1 }.mkString(",")
    } else {
      ""
    }

    val finalInferredList = List.empty[String]

    val (profileSkills, upForRedeploymentDate) = if (mailToUserProfile.contains(mail)) {
      val employeeRecommendation: EmployeeRecommendation = mailToUserProfile(mail)
      (employeeRecommendation.curatedAll.map(_.toLowerCase), employeeRecommendation.upForRedeploymentDate)

    } else {
      (List.empty[String], None)
    }

    List(recommendation.EmployeeRecommendation(
      nestedMapResult("TargetUser").toString,
      givenScore,
      profileSkills,
      profileSkills,
      finalInferredList,
      preciseInferredSkillsMap,
      finalHighlightedTerms,
      recSourceDate = Instant.parse(nestedMapResult("Date").toString),
      foundReferenceText = extractedFoundText,
      foundReferenceField = extractedFoundField,
      upForRedeploymentDate = upForRedeploymentDate))
  }

  def buildEmployeeRecommendationFromDomainTaxoSearch(mailToUserProfile: Map[String, EmployeeDetails], givenScore: Double, userProfileSearchRecord: util.HashMap[String, Any], requiredSkills: List[String], searchMappings: Boolean = false): EmployeeRecommendation = {
    //TODO: deprecate this
    val nestedMapResult = userProfileSearchRecord.asScala //asInstanceOf[Map[String,Any]]

    val mail = nestedMapResult("From").toString.toLowerCase.trim

    val profileSkills = if (mailToUserProfile.contains(mail)) {
      mailToUserProfile(mail).currated_all.map(_.toLowerCase)
    } else {
      List.empty[String]
    }

    var inferredSkillsMap: Map[String, List[String]] = Map.empty


    //In this case is necessary in order to display the inferredSkillsField for the current ui implementation
    val allInferredSkills = mutable.LinkedHashSet(inferredSkillsMap.values.flatten.toSeq: _*).toList

    EmployeeRecommendation(
      mail,
      givenScore,
      profileSkills,
      profileSkills,
      allInferredSkills,
      inferredSkillsMap,
      List.empty[String])
  }


  def buildEmployeeRecommendationFromUserProfileSearch(givenScore: Double,
                                                       userProfileSearchRecord: util.HashMap[String, Any],
                                                       requiredSkills: List[String] = List.empty,
                                                       computeInferredSkills: Boolean = true,
                                                       taxonomyList: List[TaxonomyType.Value] = List.empty,
                                                       searchMappings: Boolean = false): EmployeeRecommendation = {
    val nestedMapResult = userProfileSearchRecord.asScala //asInstanceOf[Map[String,Any]]

    val employeeMail = nestedMapResult("mail").toString

    val curatedAll = nestedMapResult("currated_all").toString.split("[,]").toList

    val availableStartingFromDateString: Option[String] = Option(nestedMapResult("hr_data_available_starting_from")).map(_.toString)

    val upForRedeploymentDate: Option[Instant] = availableStartingFromDateString
      .map(availabilityString => Instant.from(DateTimeFormatter.ISO_INSTANT.parse(availabilityString)))

    val employeeSkills = nestedMapResult("skills").toString.split(",").toList.map(_.toLowerCase)

    val employeeSkillsInferredFromDomainExpert: Map[TaxonomyType.Value, List[String]] = taxonomyList.map {
      taxoName =>
        val deFieldName = "de_" + taxoName
        (taxoName -> nestedMapResult(deFieldName).toString.split(",").toList)
    }.toMap

    val employeeHighlights: mutable.Map[String, Any] = nestedMapResult.getOrElse("@search.highlights", new java.util.LinkedHashMap[String, Any]()).asInstanceOf[util.LinkedHashMap[String, Any]].asScala

    val mappingListAsMap: Map[TaxonomyType.Value, String] = taxonomyList.map {
      taxoName =>
        val deFieldName = "de_" + taxoName.toString + "_mapping"
        (taxoName -> nestedMapResult(deFieldName).toString)
    }.toMap


    val fieldToHighlightedTerms = employeeHighlights.map {
      case (fieldName, fieldBody) =>
        val strFieldBody = fieldBody.toString
        val extractedHighlightedTerms = StringUtils.substringsBetween(strFieldBody, "<em>", "</em>").toList.map(_.toLowerCase)
        (fieldName, fieldBody, extractedHighlightedTerms)
    }

    /*
    val extractedFoundText = if (searchMappings) {
      fieldToHighlightedTerms.filter(p => p._1.toString.startsWith("de_")).map { p => p._2.toString }.toList.mkString(",")
    } else {
      ""
    }
    val extractedFoundField = if (searchMappings) {
      fieldToHighlightedTerms.filter(p => p._1.toString.startsWith("de_")).map { p => p._1 }.mkString(",")
    } else {
      ""
    }*/

    val highLightedTermsAsList = fieldToHighlightedTerms.flatMap(x => x._3).toList.distinct

    val finalHighlightedTerms = mutable.LinkedHashSet(highLightedTermsAsList ++ highLightedTermsAsList.sliding(2).map(_.mkString(" ")): _*).toList.distinct.filter(term => RecommendationEngineUtil.stopWordsSets.contains(term) == false)

    val preciseInferredSkillsMap: Map[String, List[String]] = mappingListAsMap.map {
      case (taxo: TaxonomyType.Value, mappingListAsString: String) =>
        val precisesdlyInferredSkills = buildPreciseInferredSkills(finalHighlightedTerms, mappingListAsString)
        (taxo.toString -> precisesdlyInferredSkills)
    }

    val extractedFoundText = if (searchMappings) {
      preciseInferredSkillsMap.filter(p => p._2.nonEmpty).map { p => p._1 + ":" + p._2.mkString(",") }.toList.mkString(";")
    } else {
      ""
    }
    val extractedFoundField = if (searchMappings) {
      preciseInferredSkillsMap.filter(p => p._2.nonEmpty).keys.mkString(",")
    } else {
      ""
    }

    //TODO: introduce sort here maybe
    val anyInferredSkillsDetermined = preciseInferredSkillsMap.values.exists(p => p.nonEmpty)

    val finalInferredList = List.empty[String]

    EmployeeRecommendation(
      employeeMail,
      givenScore,
      employeeSkills,
      employeeSkills,
      finalInferredList,
      preciseInferredSkillsMap,
      finalHighlightedTerms,
      foundReferenceText = extractedFoundText,
      foundReferenceField = extractedFoundField,
      upForRedeploymentDate = upForRedeploymentDate)
  }


  def buildEmployeeRecommendationFromNameSearch(givenScore: Double,
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


  /**
   * This method will retrieve the facet maps.
   * It will be called only once when initializing the web app
   *
   */
  def retrieveSearchFilterValues(searchFilterTypes: Seq[FilterType]): Seq[SearchFilterValues] = {
    val searchResultsFilters: Seq[SearchResultsFilter] = searchFilterTypes.map(SearchResultsFilter.fromType(_))
    val employeesIndexFacetFields: Seq[String] = searchResultsFilters.map(_.azSearchFacetField)

    val stage = SearchCriterionType.PROFILE_FACETS

    val top = "&$top=0"

    val facetFieldsListParam = employeesIndexFacetFields.map(p => s"facet=$p,count:9999").mkString("&")


    val search_params = "&" + facetFieldsListParam + top
    log.info(s"[recommendation_engine][retrieveFacetFieldsAndCounts][$stage] search params: $search_params")

    val searchResultTry: Map[String, Any] = try {
      val indexName = employeesIndexName()
      executeSearch(indexName, search_params, stage)
    } catch {
      case _: AzureSearchIndexNotFound =>
        //In case employee index name is obsolete, refresh index name cache and retry request to Azure Search
        val indexName = employeesIndexName(true)
        executeSearch(indexName, search_params, stage)
    }

    val rawFacetedSearchResults: Option[Any] = searchResultTry.get("@search.facets")
    val retrievedFacetValues: collection.Map[FilterType, List[String]] = rawFacetedSearchResults.map {
      searchResults: Any =>
        // this is structured as a Map[facetFieldName -> Array[Map[String,String]("value"->aGivenValueOfTheFacetField,"count"->occurrenceCountOfTheGivenValue)]]
        val rawValuesPerFacetField: mutable.Map[String, Any] = searchResults.asInstanceOf[java.util.LinkedHashMap[String, Any]].asScala
        val finalResults: mutable.Map[FilterType, List[String]] = rawValuesPerFacetField.map {
          tupleEntry: (String, Any) =>
            val facetField = tupleEntry._1
            val valuesToCountAny = tupleEntry._2.asInstanceOf[java.util.ArrayList[java.util.Map[String, String]]].asScala.toList
            val facetValues = valuesToCountAny.map {
              linkedHashMapEntry: util.Map[String, String] =>
                val value = linkedHashMapEntry.get("value").toString
                // maybe we will need this
                // val count = String.valueOf(linkedHashMapEntry.get("count"))
                // (value,count)
                value
            }.filter(p => p.trim.length > 0)
            SearchResultsFilter.fromAzSearchFacetField(facetField).filterType -> facetValues
        }
        log.info(s"[recommendation_engine][$stage] retrieved facet values for ${rawValuesPerFacetField.size} fields")

        finalResults
    }.getOrElse(Map.empty[FilterType, List[String]])

    val orderedResult = searchFilterTypes.flatMap { filterType =>
      val filterValuesOpt: Option[List[String]] = retrievedFacetValues.get(filterType)
      filterValuesOpt.map(filterValues => SearchFilterValues(filterType, filterValues))
    }

    orderedResult
  }


}






