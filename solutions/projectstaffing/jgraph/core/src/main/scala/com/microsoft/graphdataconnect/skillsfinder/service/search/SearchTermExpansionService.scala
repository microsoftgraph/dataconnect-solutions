/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service.search

import java.net.URLEncoder

import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.Unirest
import com.microsoft.graphdataconnect.skillsfinder.exceptions.{AzureSearchIndexNotFound, ServerErrorException}
import com.microsoft.graphdataconnect.skillsfinder.models.TaxonomyType
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.SkillSuggestion
import com.microsoft.graphdataconnect.skillsfinder.service.ConfigurationService
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpStatus
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Failure, Success, Try}


@Service
class SearchTermExpansionService {

  private val log: Logger = LoggerFactory.getLogger(classOf[SearchTermExpansionService])

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

  @Value("${app.rec.engine.useRelatedSuggestions}")
  var useRelatedSuggestionsParam: Boolean = false

  private def useRelatedSuggestions = {
    useRelatedSuggestionsParam
  }


  def getRecommendedSkillsWithRelatedTerms(targetSkillPrefix: String, taxonomyList: List[TaxonomyType.Value]): Seq[SkillSuggestion] = {
    val skillPrefix = if (targetSkillPrefix.length > 99) { //Azure limitation
      targetSkillPrefix.substring(0, 99)
    }
    else {
      targetSkillPrefix
    }
    if (useRelatedSuggestions) {
      val recommendedSkills = getSkillRecommendations(skillPrefix, taxonomyList)
      var alreadyPresentTerms = recommendedSkills.toSet
      val finalList = new mutable.ListBuffer[SkillSuggestion]
      recommendedSkills.foreach {
        recommendedSkillFullLength =>
          val recommendedSkill = if (recommendedSkillFullLength.length > 99) {
            recommendedSkillFullLength.substring(0, 99)
          } else {
            recommendedSkillFullLength
          }

          val termsRelatedToAutocompleteTerm = getTermsRelatedToRecommendedTerm(recommendedSkill, alreadyPresentTerms, taxonomyList)
          finalList += SkillSuggestion(recommendedSkill, termsRelatedToAutocompleteTerm)
          alreadyPresentTerms ++= termsRelatedToAutocompleteTerm.toSet
      }
      finalList.toList
    } else {
      getSkillRecommendations(skillPrefix, taxonomyList).map {
        recommendedSkill => SkillSuggestion(recommendedSkill, List.empty[String])
      }
    }
  }

  private def getSkillRecommendations(skillPrefix: String, taxonomyList: List[TaxonomyType.Value]): List[String] = {
    val startTime = System.nanoTime()
    try {
      val de_fields = taxonomyList.map(p => "de_" + p.toString).mkString(",")
      val allSearchFields = List("skills", de_fields, "about_me").filter(p => p.trim.nonEmpty).mkString(",")
      val searchFields = "&searchFields=" + allSearchFields
      val top = "&$top=7"
      val stage = "autocomplete"
      val suggesterName = "&suggesterName=sg"
      val searchString = "&search=" + URLEncoder.encode(skillPrefix, "utf-8") //.replace(" ", "%20")
      var indexName = employeesIndexName()
      val searchUrl = this.azureSearchBaseUrl + "/indexes/" + indexName + "/docs/autocomplete" + this.api_version + searchFields + suggesterName + searchString + top

      //log.info(s"[recommendation_engine][${stage}] search_string unencoded: ${requiredSkills.mkString(" ")}")
      log.info(s"[recommendation_engine][$stage] search_string encoded: $searchString")
      log.info(s"[recommendation_engine][$stage] searchFields : $searchFields")
      log.info(s"[recommendation_engine][$stage] top(limit) : $top")
      log.info(s"[recommendation_engine][$stage] url : $searchUrl")


      val autoCompleteHttpResponseTry: Try[Map[String, Any]] = Try {
        Unirest.get(searchUrl).headers(this.headers.asJava).asString()
      }.map {
        response =>
          if (response.getStatus == HttpStatus.SC_NOT_FOUND) {
            //In case employee index name is obsolete, refresh index name cache and retry request to Azure Search
            indexName = employeesIndexName(true)
            val searchUrl = this.azureSearchBaseUrl + "/indexes/" + indexName + "/docs/autocomplete" + this.api_version + searchFields + suggesterName + searchString + top
            log.info(s"[recommendation_engine][$stage] url : $searchUrl")
            Unirest.get(searchUrl).headers(this.headers.asJava).asString()
          } else response
      }
        .map {
          response =>
            if (response.getStatus == HttpStatus.SC_OK) {
              val jsonResponse = response.getBody
              val mapper = new ObjectMapper()
              val res: Map[String, Any] = mapper.readValue(jsonResponse, classOf[java.util.HashMap[String, Any]]).asScala.toMap
              Success(res)
            }
            else {
              val errorMessage = Try(response.getBody).getOrElse("") + Option(response.getStatusText).getOrElse("") + " http_response_code: " + Option(response.getStatus).getOrElse(0).toString
              if (response.getStatus == HttpStatus.SC_NOT_FOUND) {
                log.error(errorMessage)
                Failure(new AzureSearchIndexNotFound(indexName))
              } else {
                Failure(new ServerErrorException(s"[recommendation_engine][exception][$stage]Search request failed for $searchString with message: $errorMessage"))
              }
            }

        }.flatten

      val res: Option[List[String]] = autoCompleteHttpResponseTry match {
        case Success(responseMap) =>
          val buildUpList: Option[List[String]] = responseMap.get("value").map {
            searchResults: Any =>
              val convertedSearchResults = searchResults.asInstanceOf[java.util.ArrayList[java.util.HashMap[String, Any]]].asScala
              log.info(s"[recommendation_engine][$stage] length autocomplete results : ${convertedSearchResults.length}")

              convertedSearchResults.
                map {
                  nestedResult =>
                    List(nestedResult.get("text").toString) ++ List(nestedResult.get("queryPlusText").toString)
                }.toList.flatten
          }

          val deduplicatedTerms = buildUpList.getOrElse(List.empty).distinct
          Some(deduplicatedTerms)

        case Failure(exception) =>
          throw exception
      }

      res.getOrElse(Nil)
    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000
      log.info(s"[recommendation_engine][getTermsRecommendations][total_time]: $diffTime")
    }

  }

  private def getTermsRelatedToRecommendedTerm(term: String, alreadyPresentTerms: Set[String] = Set(), taxonomyList: List[TaxonomyType.Value]): List[String] = {
    val startTime = System.nanoTime()
    val stage = "suggest"
    try {
      val deFields = taxonomyList.map(p => "de_" + p.toString).mkString(",")
      val deMappingFields = taxonomyList.map(p => "de_" + p.toString + "_mapping").mkString(",")
      val searchFields = "&$select=" + deMappingFields + "&searchFields=" + f"skills,${deFields},about_me," + f"responsibilities,${deMappingFields}"
      val top = "&$top=1"
      val suggesterName = "&suggesterName=sg"
      val searchString = "&search=" + URLEncoder.encode(term, "utf-8") //.replace(" ", "%20")

      val searchUrl = this.azureSearchBaseUrl + "/indexes/" + employeesIndexName() + "/docs/suggest" + this.api_version + searchFields + suggesterName + searchString + top

      //log.info(s"[recommendation_engine][${stage}] search_string unencoded: ${requiredSkills.mkString(" ")}")
      log.info(s"[recommendation_engine][getTermsRelatedToRecommendedTerm][$stage] search_string encoded: $searchString")
      log.info(s"[recommendation_engine][getTermsRelatedToRecommendedTerm][$stage] searchFields : $searchFields")
      log.info(s"[recommendation_engine][getTermsRelatedToRecommendedTerm][$stage] top(limit) : $top")
      log.info(s"[recommendation_engine][getTermsRelatedToRecommendedTerm][$stage] url : $searchUrl")


      val suggestHttpResponse: Map[String, Any] = Try {
        Unirest.get(searchUrl).headers(this.headers.asJava).asString()
      }.map {
        response =>
          if (response.getStatus == HttpStatus.SC_NOT_FOUND) {
            //In case employee index name is obsolete, refresh index name cache and retry request to Azure Search
            val searchUrl = this.azureSearchBaseUrl + "/indexes/" + employeesIndexName() + "/docs/suggest" + this.api_version + searchFields + suggesterName + searchString + top
            log.info(s"[recommendation_engine][getTermsRelatedToRecommendations][$stage] url : $searchUrl")
            Unirest.get(searchUrl).headers(this.headers.asJava).asString()
          } else response
      }
        .map {
          response =>
            if (response.getStatus == HttpStatus.SC_OK) {
              val jsonResponse = response.getBody
              val mapper = new ObjectMapper()
              val res: Map[String, Any] = mapper.readValue(jsonResponse, classOf[java.util.HashMap[String, Any]]).asScala.toMap
              res
            }
            else {
              val errorMessage = Try(response.getBody).getOrElse("") + Option(response.getStatusText).getOrElse("") + " http_response_code: " + Option(response.getStatus).getOrElse(0).toString
              log.error(s"[recommendation_engine][getTermsRelatedToRecommendedTerm][$stage] Could not send request to azure search at $searchUrl ", errorMessage)
              Map.empty[String, Any]
            }
        }.getOrElse(Map.empty[String, Any])

      val res: List[String] = suggestHttpResponse.get("value").map {
        searchResults: Any =>
          val convertedSearchResults = searchResults.asInstanceOf[java.util.ArrayList[java.util.HashMap[String, Any]]].asScala
          log.info(s"[recommendation_engine][getTermsRelatedToRecommendedTerm][$stage] length suggest results : ${convertedSearchResults.length}")

          convertedSearchResults.
            map {
              nestedResult =>
                //TODO: fix this one
                taxonomyList.map {
                  p =>
                    val mappingFieldName = "de_" + p.toString + "_mapping"
                    nestedResult.get(mappingFieldName).toString
                }
            }.toList.flatten
      }.getOrElse(List.empty)

      if (res.isEmpty) {
        List.empty[String]
      } else {
        val retrievedText = res.head

        if (retrievedText.contains(":")) {

          val mappingTermToRelatedTerms: Map[String, mutable.LinkedHashSet[String]] = retrievedText.split(";").map {
            line =>
              val tokens = line.split("[:]")
              val key = tokens.head
              val values = mutable.LinkedHashSet(tokens.tail.toList.mkString(":").split("[,]").toSeq: _*)
              (key, values)
          }.toMap

          if (mappingTermToRelatedTerms.contains(term)) {
            return mappingTermToRelatedTerms(term).toList.filter(p => alreadyPresentTerms.contains(p) == false).take(5)
          }

          val collectedRelatedTerms = mutable.ListBuffer[String]()
          mappingTermToRelatedTerms.foreach {
            case (key: String, associatedTerms: mutable.LinkedHashSet[String]) =>
              val allRelatedTerms = List(key) ++ associatedTerms.toList

              val markedLine = allRelatedTerms.map {
                relatedTerm =>
                  if (StringUtils.equalsIgnoreCase(term, relatedTerm)
                    || relatedTerm.startsWith(term + " ")
                    || relatedTerm.startsWith(term + "-")
                    || relatedTerm.startsWith(term + ".")
                    || relatedTerm.endsWith(" " + term)
                    || relatedTerm.endsWith("-" + term)
                    || relatedTerm.endsWith("." + term)
                    || relatedTerm.contains("." + term + ".")
                    || relatedTerm.contains(" " + term + " ")
                    || relatedTerm.contains("-" + term + "-")) {
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

          collectedRelatedTerms.toList.filter(p => p.equalsIgnoreCase(term) == false).filter(p => alreadyPresentTerms.contains(p) == false).take(5)
        }
        else {
          val extractedTokens = mutable.LinkedHashSet(retrievedText.split("[,]").toSeq: _*)
          extractedTokens.toList.filter(p => p.equalsIgnoreCase(term) == false).filter(p => alreadyPresentTerms.contains(p) == false).take(5)
        }
      }
    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000
      log.info(s"[recommendation_engine][$stage][total_time]: $diffTime")
    }

  }

}






