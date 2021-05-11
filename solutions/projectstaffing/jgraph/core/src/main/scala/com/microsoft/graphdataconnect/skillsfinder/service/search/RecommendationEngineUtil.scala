/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service.search

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{Instant, ZoneOffset, ZonedDateTime}

import com.microsoft.graphdataconnect.skillsfinder.models.{FilterType, SearchResultsFilter, SearchSettingsRequestResponse, SearchWeightingOrder}

object RecommendationEngineUtil {

  val stopWordsSets = Set("the", "and", "or", "n", "in",
    "a", "about", "an", "are", "as", "at", "be", "by", "com",
    "en", "for", "from", "how", "i", "in", "is", "la", "of",
    "on", "or", "that", "the", "this", "to", "was", "what",
    "when", "where", "who", "will", "with", "und", "the"
  )

  def buildFacetFilterParamForPostRequest(searchFilterValues: Map[FilterType, List[String]],
                                          employeeAddressesToBeIgnored: Set[String] = Set(),
                                          requestedEmployeeAddresses: Set[String] = Set(),
                                          availableAtTheLatestOn: Option[String] = None,
                                          availabilityStartLimit: Option[String] = None) = {
    val filter = if (searchFilterValues.isEmpty) {
      ""
    }
    else {
      val facetFiltersListAsBooleanExpression = searchFilterValues.map {
        facetToValues =>
          val facetName = SearchResultsFilter.fromType(facetToValues._1).azSearchFacetField
          val facetValues = facetToValues._2
          //TODO: how to facet on quotes,... etc
          val facetValuePairsList = facetValues.map { faceValue => faceValue.replace("'", "''").replace("\"", "\"\"") }.
            map { facetValue => s"(${facetName} eq '${facetValue}')" }
          val facetValuePairs = if (facetValuePairsList.length > 1) {
            "(" + facetValuePairsList.mkString(" or ") + ")"
          }
          else {
            facetValuePairsList.head
          }
          facetValuePairs
      }
      //"&$filter=" + URLEncoder.encode(facetFiltersListAsBooleanExpression.mkString(" and "), "utf-8")
      facetFiltersListAsBooleanExpression.mkString(" and ")
    }

    val filterOutStaleEmployees = if (employeeAddressesToBeIgnored.nonEmpty) "(not search.in(mail, '" + employeeAddressesToBeIgnored.mkString(",") + "', ','))" else ""
    val filterForEmployees = if (requestedEmployeeAddresses.nonEmpty) "(search.in(mail, '" + requestedEmployeeAddresses.mkString(",") + "', ','))" else ""
    val availabilityFilter = if (requestedEmployeeAddresses.isEmpty) {
      val availabilityFilterEndLimit =
        if (availableAtTheLatestOn.nonEmpty) {
          val date = if (availableAtTheLatestOn.get.contains("T00:00:00Z")) availableAtTheLatestOn.get else availableAtTheLatestOn.get + "T00:00:00Z"
          val zdt = ZonedDateTime.parse(date)
          val formattedDate = DateTimeFormatter.ISO_INSTANT.format(zdt)
          s"(hr_data_available_starting_from le $formattedDate)"
        } else ""

      val availabilityFilterStartLimit =
        if (availabilityStartLimit.nonEmpty) {
          val stringDate = if (availabilityStartLimit.get.contains("T00:00:00Z")) availabilityStartLimit.get else availabilityStartLimit.get + "T00:00:00Z"
          val zdt = ZonedDateTime.parse(stringDate)
          val formattedDate = DateTimeFormatter.ISO_INSTANT.format(zdt)
          s"(hr_data_available_starting_from ge $formattedDate)"
        } else ""

      val availabilityNullFilter = if (availabilityFilterStartLimit.isEmpty) "(hr_data_available_starting_from eq null)" else ""

      val availabilityLimitsFilter = List(availabilityFilterEndLimit, availabilityFilterStartLimit).filter(_.nonEmpty).mkString(" and ")

      "(" + List(availabilityLimitsFilter, availabilityNullFilter).filter(_.nonEmpty).mkString(" or ") + ")"
    } else ""

    List(filter, filterOutStaleEmployees, filterForEmployees, availabilityFilter).filter(_.nonEmpty).mkString(" and ")
  }

  def buildFacetFilterParamForGetRequest(searchFilterValues: Map[FilterType, List[String]]) = {
    if (searchFilterValues.isEmpty) {
      ""
    }
    else {
      val facetFiltersListAsBooleanExpression = searchFilterValues.map {
        facetToValues =>
          val facetName = SearchResultsFilter.fromType(facetToValues._1).azSearchFacetField
          val facetValues = facetToValues._2
          val facetValuePairsList = facetValues.map { faceValue => faceValue.replace("'", "''").replace("\"", "\"\"") }.
            map { facetValue => s"(${facetName}%20eq%20'${facetValue.replace(" ", "%20")}')" }
          val facetValuePairs = if (facetValuePairsList.length > 1) {
            "(" + facetValuePairsList.mkString("%20or%20") + ")"
          }
          else {
            facetValuePairsList.head
          }
          facetValuePairs
      }
      //"&$filter=" + URLEncoder.encode(facetFiltersListAsBooleanExpression.mkString(" and "), "utf-8")
      "&$filter=" + facetFiltersListAsBooleanExpression.mkString("%20and%20")

    }
  }

  def expandQueryList(requiredSkills: List[String]): List[String] = {
    val optimizedRequiredSkills = requiredSkills
      .map(x => x.toLowerCase.trim)
      .flatMap(x => x.split("[,]|[;]").toList.filter(p => p.trim.nonEmpty))
      .map {
        skill =>
          if (skill.trim().split(" ").length > 1) {
            /**
             * From machine learning we also do: machine-learning
             */
            val dash_skill = skill.trim().split(" ").mkString("-")
            val joined_skill = skill.trim().split(" ").mkString("")
            "\"" + skill + "\"" + " \"" + dash_skill + "\"" + " " + joined_skill + " " + skill
          }

          else if (skill.trim().split("-").length > 1) {
            /**
             * From machine-learning we do: machine learning
             */
            val space_skill = skill.trim().split("-").mkString(" ")
            val joined_skill = skill.trim().split(" ").mkString("")
            "\"" + skill + "\"" + " \"" + space_skill + "\"" + " " + joined_skill + " " + skill
          }
          else if (skill.contains("+") || skill.contains(".")) {
            "\"" + skill + "\""
          }
          else {
            skill
          }
      }
    optimizedRequiredSkills
  }


  def expandQueryListToHighlightingCombinations(requiredSkills: List[String]): List[String] = {
    val optimizedRequiredSkills = requiredSkills
      .map(x => x.toLowerCase.trim)
      .flatMap(x => x.split("[,]|[;]").toList.filter(p => p.trim.nonEmpty))
      .flatMap {
        skill =>
          if (skill.trim().split(" ").length > 1) {
            /**
             * From machine learning we also do: machine-learning
             */
            val dash_skill = skill.trim().split(" ").mkString("-")
            val joined_skill = skill.trim().split(" ").mkString("")
            List(skill, dash_skill, joined_skill, skill) ++ skill.trim().split(" ").toList
          }

          else if (skill.trim().split("-").length > 1) {
            /**
             * From machine-learning we do: machine learning
             */
            val space_skill = skill.trim().split("-").mkString(" ")
            val joined_skill = skill.trim().split(" ").mkString("")
            List(skill, space_skill, joined_skill, skill) ++ skill.trim().split("-").toList
          }
          else {
            List(skill)
          }
      }
    optimizedRequiredSkills.filter(skillTerm => stopWordsSets.contains(skillTerm) == false)
  }

  def getOrderProcessingList(searchRelevanceWeight: Double,
                             searchFreshnessWeight: Double,
                             searchVolumeWeight: Double,
                             lengthOfTheRecommendationList: Int): List[(SearchWeightingOrder.Value, Int)] = {

    val relevanceWeight = if (searchRelevanceWeight == 0 && searchFreshnessWeight == 0 && searchVolumeWeight == 0) {
      1
    }
    else {
      searchRelevanceWeight
    }

    val freshnessWeight = searchFreshnessWeight
    val volumeWeight = searchVolumeWeight


    val numberOfRecordsSelectedByRelevance = math.ceil(relevanceWeight * lengthOfTheRecommendationList).toInt
    val numberOfRecordsSelectedByVolume = math.ceil(volumeWeight * lengthOfTheRecommendationList).toInt
    val numberOfRecordsSelectedByFreshness = math.ceil(freshnessWeight * lengthOfTheRecommendationList).toInt


    val orderedProcessingOrder: List[(SearchWeightingOrder.Value, Int)] = List(
      (SearchWeightingOrder.RELEVANCE, numberOfRecordsSelectedByRelevance),
      (SearchWeightingOrder.VOLUME, numberOfRecordsSelectedByVolume),
      (SearchWeightingOrder.FRESHNESS, numberOfRecordsSelectedByFreshness)).
      sortBy(x => x._2).reverse
    orderedProcessingOrder

  }

  def buildTimeFilterParam(searchSettings: SearchSettingsRequestResponse,
                           requestedEmployeeAddresses: Set[String] = Set(),
                           emailFilterField: String = ""): String = {
    val beginDate = if (searchSettings.freshnessBeginDateEnabled && searchSettings.freshnessBeginDate != null) {
      searchSettings.freshnessBeginDate.atZoneSameInstant(ZoneOffset.UTC).toInstant
    }
    else {
      Instant.now().minus(365 * 20, ChronoUnit.DAYS) //20 years behind
    }

    val toExcludeDomainsList = if (searchSettings.excludedEmailDomainsEnabled) {
      if (searchSettings.excludedEmailDomains != null) {
        val domainList = searchSettings.excludedEmailDomains
          .map(emailDomain => emailDomain.toLowerCase.trim)
          .flatMap(emailDomain => List(emailDomain.split("\\s+|[,]|[;]")).flatten.filter(p => p.trim.nonEmpty))
          .filter(emailDomain => emailDomain.trim.length > 1)
          .filter(emailDomain => emailDomain.contains(" ") == false)
          .filter {
            emailDomain =>
              val allowedChacacters = Set("-", ".")
              val toExclude = emailDomain.map {
                domainCharacter =>
                  Character.isLetter(domainCharacter) || Character.isDigit(domainCharacter) || allowedChacacters.contains(domainCharacter.toString)
              }.exists(p => p == false)
              !toExclude
          }
        domainList

      }
      else {
        List.empty[String]
      }
    } else {
      List.empty[String]
    }

    val toIncludeDomainsList = if (searchSettings.includedEmailDomainsEnabled) {
      if (searchSettings.includedEmailDomains != null) {
        searchSettings.includedEmailDomains
          .map(emailDomain => emailDomain.toLowerCase.trim)
          .flatMap(emailDomain => List(emailDomain.split("\\s+|[,]|[;]")).flatten.filter(p => p.trim.nonEmpty))
          .filter(emailDomain => emailDomain.trim.length > 1)
          .filter(emailDomain => emailDomain.contains(" ") == false)
          .filter {
            emailDomain =>
              val allowedChacacters = Set("-", ".")
              val res = emailDomain.map {
                domainCharacter =>
                  Character.isLetter(domainCharacter) || Character.isDigit(domainCharacter) || allowedChacacters.contains(domainCharacter.toString)
              }

              val toExclude = emailDomain.map {
                domainCharacter =>
                  Character.isLetter(domainCharacter) || Character.isDigit(domainCharacter) || allowedChacacters.contains(domainCharacter.toString)
              }.exists(p => p == false)
              !toExclude
          }

      }
      else {
        List.empty[String]
      }
    } else {
      List.empty[String]
    }

    val filterParam = {
      val dateBaseLine = DateTimeFormatter.ISO_INSTANT.format(beginDate.atZone(ZoneOffset.UTC).withHour(0).withMinute(0).withSecond(0).withNano(0))
      val excludeDomainFilterExpression: String = if (toExcludeDomainsList.nonEmpty) {
        toExcludeDomainsList.map {
          domainName =>
            s"(search.ismatch('$domainName','From','full','any') eq false)"
        }.mkString(" and ")
      }
      else ""

      val includedDomainFilterExpression = if (toIncludeDomainsList.nonEmpty) {
        val resultedFilterExpression = toIncludeDomainsList.map {
          domainName =>
            s"search.ismatch('$domainName','From','full','any')"
        }.mkString(" or ")

        if (toIncludeDomainsList.length > 1 && resultedFilterExpression.trim.length > 1) {
          "(" + resultedFilterExpression + ")"
        } else {
          resultedFilterExpression
        }

      }
      else {
        ""
      }

      val filterForEmployees = if (requestedEmployeeAddresses.nonEmpty) s"(search.in($emailFilterField, '" + requestedEmployeeAddresses.mkString(",") + "', ','))" else ""

      val finalExpression = List(includedDomainFilterExpression, excludeDomainFilterExpression, filterForEmployees).filter(_.nonEmpty).mkString(" and ")

      if (finalExpression.nonEmpty) {
        finalExpression + s" and (Date ge $dateBaseLine)"
      }
      else {
        s"Date ge $dateBaseLine"
      }
    }

    filterParam
  }

  /**
   * Extract the weights coming from the UI.
   * EG: relevance:40,freshness:20,volume:20
   * then:
   * relevanceWeight: 50%, freshness: 25%, volumne: 25%
   *
   * @param searchSettings
   * @return
   */
  def extractSearchCriteriaWeight(searchSettings: SearchSettingsRequestResponse) = {
    val passedRelevanceScore: Int = if (searchSettings.relevanceScoreEnabled == false) {
      0
    } else {
      searchSettings.relevanceScore
    }

    val passedFreshnessScore: Int = if (searchSettings.freshnessEnabled == false) {
      0
    } else {
      searchSettings.freshness
    }

    val passedVolumesScore: Int = if (searchSettings.volumeEnabled == false) {
      0
    } else {
      searchSettings.volume
    }

    val total = if ((passedRelevanceScore + passedFreshnessScore + passedVolumesScore).toDouble == 0) {
      1
    } else {
      (passedRelevanceScore + passedFreshnessScore + passedVolumesScore).toDouble
    }

    val searchRelevanceWeight = passedRelevanceScore / total
    val searchFreshnessWeight = passedFreshnessScore / total
    val searchVolumeWeight = passedVolumesScore / total
    (searchRelevanceWeight, searchFreshnessWeight, searchVolumeWeight)
  }

}
