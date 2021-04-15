package com.microsoft.graphdataconnect.skillsfinder.service.search

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.{HttpResponse, Unirest}
import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.SearchCriterionType
import com.microsoft.graphdataconnect.skillsfinder.exceptions.{AzureSearchIndexNotFound, ServerErrorException}
import com.microsoft.graphdataconnect.skillsfinder.logging.ConditionalLogger
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.EmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.recommendation.EmployeeRecommendation
import com.microsoft.graphdataconnect.skillsfinder.models.{FilterType, SearchSettingsRequestResponse, TaxonomyType}
import com.microsoft.graphdataconnect.skillsfinder.service.ConfigurationService
import org.apache.http.HttpStatus
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}

import scala.collection.JavaConverters._
import scala.collection.{immutable, mutable}
import scala.util.{Failure, Success, Try}

trait PaginationService {

  private val log: Logger = LoggerFactory.getLogger(classOf[PaginationService])

  @Value("${logging.enable.recommendation.reasoning.logs}")
  var enableRecommendationReasoningLogs: Boolean = true

  protected lazy val conditionalLogger: ConditionalLogger = new ConditionalLogger(enableRecommendationReasoningLogs)

  @Value("${gdc-search-service-key:#{environment.AZURE_SEARCH_APIKEY}}")
  var azureSearchApiKey: String = _

  lazy val headers = Map("Content-Type" -> "application/json", "api-key" -> azureSearchApiKey)

  @Value("${azure.search.baseUrl}")
  var azureSearchBaseUrl = "https://gdcdemosearch.search.windows.net"

  val api_version = "?api-version=2020-06-30"

  @Autowired
  var configurationService: ConfigurationService = _

  @Autowired
  var recommendationService: RecommendationService = _

  def employeesIndexName(refreshIndexConfigCache: Boolean = false): String = {
    configurationService.getAzureSearchEmployeeIndexNameFromCache(refreshIndexConfigCache)
  }

  def employeesIndexUrl(refreshIndexConfigCache: Boolean = false): String = s"$azureSearchBaseUrl/indexes/" + employeesIndexName(refreshIndexConfigCache)


  //TODO should be removed, no longer used.
  def retrieveMailAddressesToEmployeeDetailsMap(limit: Int = 2000): Map[String, EmployeeDetails] = {
    val startTime = System.nanoTime()
    try {
      val searchString = "&queryType=simple&$select=currated_all,mail,hr_data_available_starting_from"
      val top = "&$top=" + limit.toString

      val searchUrl = employeesIndexUrl() + "/docs" + this.api_version + searchString + top
      log.info(s"[recommendation_engine][retrieveMailAddressesToEmployeeDetailsMap] searching on: ${searchUrl}")
      val final_res: Option[Map[String, EmployeeDetails]] = Try {
        Try {
          Unirest.get(searchUrl).headers(this.headers.asJava).asString()
        }.map {
          response =>
            if (response.getStatus == HttpStatus.SC_NOT_FOUND) {
              //In case employee index name is obsolete, refresh index name cache and retry request to Azure Search
              val searchUrl = employeesIndexUrl(true) + "/docs" + this.api_version + searchString + top
              log.info(s"[recommendation_engine][retrieveMailAddressesToEmployeeDetailsMap] searching on: ${searchUrl}")
              Unirest.get(searchUrl).headers(this.headers.asJava).asString()
            }
            else response
        }.get
      } match {
        case Success(response) =>
          if (response.getStatus == HttpStatus.SC_OK) {
            val jsonResponse = response.getBody

            val startTimeProcessingJson = System.nanoTime()
            val mapper = new ObjectMapper()
            val res = mapper.readValue(jsonResponse, classOf[java.util.HashMap[String, Any]]).asScala.toMap

            val buildUpList = res.get("value").map {
              searchResults: Any =>
                val convertedSearchResults = searchResults.asInstanceOf[java.util.ArrayList[java.util.HashMap[String, Any]]].asScala
                log.info(s"[recommendation_engine][retrieveMailAddressesToEmployeeDetailsMap] length search results : ${convertedSearchResults.length}")

                convertedSearchResults.
                  map {
                    nestedResult =>

                      val nestedMapResult: mutable.Map[String, Any] = nestedResult.asScala
                      val employeeMail = nestedMapResult("mail").toString.trim.toLowerCase
                      val employeeCuratedAll = nestedMapResult("currated_all").toString.split("[,]").toList
                      val availableStartingFromDate: Option[String] = if (nestedMapResult.contains("hr_data_available_starting_from")) Option(nestedMapResult("hr_data_available_starting_from")).map(_.toString) else None

                      employeeMail -> EmployeeDetails("", "", "", List.empty[String], List.empty[String],
                        List.empty[String], List.empty[String], employeeCuratedAll, upForRedeploymentDate = availableStartingFromDate)

                  }.toMap

            }

            val diffTimeProcessingJson = (System.nanoTime() - startTimeProcessingJson) / 1000000
            log.info(s"[recommendation_engine][retrieveMailAddressesToEmployeeDetailsMap][json_process][total_time]: $diffTimeProcessingJson")
            buildUpList
          }
          else {
            val errorMessage = Try(response.getBody).getOrElse("") + Option(response.getStatusText).getOrElse("") + " http_response_code: " + Option(response.getStatus).getOrElse(0).toString

            log.error(s"[recommendation_engine][retrieveMailAddressesToEmployeeDetailsMap] Could not send request to azure search at $searchUrl ", errorMessage)

            Some(Map.empty[String, EmployeeDetails])
          }
        case Failure(exception) =>
          log.error(s"[recommendation_engine][retrieveMailAddressesToEmployeeDetailsMap] Could not send request to azure search at $searchUrl ", exception)
          Some(Map.empty[String, EmployeeDetails])

      }
      final_res.getOrElse(Map.empty[String, EmployeeDetails])
    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000
      log.info(s"[recommendation_engine][retrieveMailAddressesToEmployeeDetailsMap][total_time]: $diffTime")
    }
  }

  implicit def searchCriterionTypeToOption(stage: SearchCriterionType): Option[SearchCriterionType] = Option(stage)

  implicit def searchCriterionTypeOptionToSearchCriterionType(stageOpt: Option[SearchCriterionType]): SearchCriterionType = stageOpt.getOrElse(SearchCriterionType.PROFILE_SKILLS)

  def searchCriterionTypeToString(stage: Option[SearchCriterionType]): String = stage.map(stage => s"[${stage.toString}]").getOrElse("")

  def performSearchOnUserIndex(requiredSkills: List[String],
                               fieldName: String = "skills",
                               stage: Option[SearchCriterionType] = None,
                               givenScore: Double = 10,
                               searchReceivers: Boolean = false,
                               skip: Int = 0,
                               //TODO change the default value to the total number of employees (get it from the db)
                               top: Int = 2000,
                               taxonomyList: List[TaxonomyType.Value] = List.empty,
                               searchMappings: Boolean = false,
                               searchFilterValues: Map[FilterType, List[String]] = Map.empty,
                               employeeAddressesToBeIgnored: Set[String] = Set(),
                               requestedEmployeeAddresses: Set[String] = Set(),
                               availableAtTheLatestOn: Option[String] = None,
                               availabilityStartLimit: Option[String] = None,
                               sortByAvailabilityAndName: Boolean = false): (Set[String], List[EmployeeRecommendation]) = {

    val startTime = System.nanoTime()

    try {

      val optimizedRequiredSkills = RecommendationEngineUtil.expandQueryList(requiredSkills)

      //if phrase search is used (e.g. "data sciens") we have to escape the double quotes in the string.
      // See: https://docs.microsoft.com/en-us/azure/search/query-simple-syntax#keyword-search-on-terms-and-phrases
      val searchString = optimizedRequiredSkills.mkString(" ").replaceAll("\"", "\\\\\"")

      //TODO: modify this:
      var allFields = taxonomyList.map(p => "de_" + p.toString)
      val allFieldsStr = (List(fieldName) ++ allFields).mkString(",")

      //val searchFields = "&$select="+allFieldsStr+s"&searchFields=$fieldName"

      val filterParam = RecommendationEngineUtil.buildFacetFilterParamForPostRequest(searchFilterValues, employeeAddressesToBeIgnored, requestedEmployeeAddresses, availableAtTheLatestOn, availabilityStartLimit)

      log.info(s"[recommendation_engine]${searchCriterionTypeToString(stage)} search_string unencoded: ${requiredSkills.mkString(" ")}")

      val searchResultTry: Map[String, Any] =
        try {
          val indexName = employeesIndexName()
          executeSearchWithPost(indexName = employeesIndexName(),
            searchString = searchString,
            filterParam = filterParam,
            facetQueryParam = s"mail,count:${skip + 2 * top}",
            searchFields = fieldName,
            highlightFields = fieldName,
            top = top,
            skip = skip,
            stage = stage,
            sortByAvailabilityAndName = sortByAvailabilityAndName)
        } catch {
          case _: AzureSearchIndexNotFound =>
            //In case employee index name is obsolete, refresh index name cache and retry request to Azure Search
            executeSearchWithPost(indexName = employeesIndexName(true),
              searchString = searchString,
              filterParam = filterParam,
              facetQueryParam = s"mail,count:${skip + 2 * top}",
              searchFields = fieldName,
              highlightFields = fieldName,
              top = top,
              skip = skip,
              stage = stage,
              sortByAvailabilityAndName = sortByAvailabilityAndName)
        }

      val retrievedSearchResults: List[EmployeeRecommendation] = searchResultTry.get("value").map {
        searchResults: Any =>
          val convertedSearchResults = searchResults.asInstanceOf[java.util.ArrayList[java.util.HashMap[String, Any]]].asScala
          log.info(s"[recommendation_engine]${searchCriterionTypeToString(stage)} length search results : ${convertedSearchResults.length}")

          convertedSearchResults.
            map {
              nestedResult =>
                recommendationService.buildEmployeeRecommendationFromUserProfileSearch(givenScore, nestedResult, requiredSkills, taxonomyList = taxonomyList, searchMappings = searchMappings)
            }.toList
      }.getOrElse(Nil)

      val emailAddressFacet: Set[String] = {
        searchResultTry.get("@search.facets").map {
          searchResults: Any =>
            val convertedSearchResults = searchResults.asInstanceOf[util.HashMap[String, Any]].asScala.toMap
            val targetUsers = convertedSearchResults("mail").asInstanceOf[util.ArrayList[util.HashMap[String, Any]]].asScala.toList

            val selectedUsers: Set[String] = targetUsers.map(userCountMap => userCountMap.get("value").toString.trim.toLowerCase).toSet

            selectedUsers

        }.getOrElse(Set[String]())
      }

      val selectedUsers = new scala.collection.mutable.HashSet[String]()
      val finalEmployees = new scala.collection.mutable.ListBuffer[EmployeeRecommendation]()


      retrievedSearchResults.foreach(scoredEmployee => {
        finalEmployees += scoredEmployee
        selectedUsers += scoredEmployee.email.toLowerCase
        if (requiredSkills.nonEmpty) {
          //log recommendation explanations only if there are search terms given
          recommendationService.explainEmployeeRecommendation(scoredEmployee, searchMappings, fieldName, stage)
          conditionalLogger.info(s"[recommendation_engine][$stage] result: $scoredEmployee")
        }
      })

      log.info(s"[recommendation_engine]${searchCriterionTypeToString(stage)} recommendation list length : ${finalEmployees.toList.length}")
      (emailAddressFacet, finalEmployees.toList)
    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000
      log.info(s"[recommendation_engine][performSearchOnUserIndex]${searchCriterionTypeToString(stage)}[total_time]: $diffTime")
    }
  }

  def executeSearch(indexName: String, search_params: String, stage: SearchCriterionType): Map[String, Any] = {

    val startTime = System.nanoTime()
    try {
      val searchUrl = this.azureSearchBaseUrl + "/indexes/" + indexName + "/docs" + this.api_version + search_params
      log.info(s"[recommendation_engine]${searchCriterionTypeToString(stage)} url : $searchUrl")

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
            throw new ServerErrorException(s"[recommendation_engine][exception]${searchCriterionTypeToString(stage)}Search request failed for $search_params. Error message: $errorMessage")
          }

        case Failure(exception) =>
          throw new ServerErrorException(s"[recommendation_engine][exception]${searchCriterionTypeToString(stage)}Search request failed for $search_params. Error message: ${exception.getMessage}")
      }

    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000000
      log.info(s"[recommendation_engine][execute_search]${searchCriterionTypeToString(stage)}[total_time]: $diffTime")
    }
  }

  def executeSearchWithPost(indexName: String,
                            searchString: String,
                            filterParam: String = "",
                            facetQueryParam: String = "",
                            searchFields: String,
                            highlightFields: String,
                            top: Int,
                            skip: Int,
                            stage: Option[SearchCriterionType],
                            sortByAvailabilityAndName: Boolean = false): Map[String, Any] = {

    val body =
      s"""
         |{
         |     "count": true,
         |     "facets": ["$facetQueryParam"],
         |     "filter": "$filterParam",
         |     "highlight": "$highlightFields",
         |     "search": "$searchString",
         |     "searchFields": "$searchFields",
         |     "skip": $skip,
         |     "top": $top,
         |     "orderby": "${if (sortByAvailabilityAndName) "hr_data_available_starting_from, display_name" else ""}"
         |}  """.stripMargin

    val startTime = System.nanoTime()
    try {
      val searchUrl = this.azureSearchBaseUrl + "/indexes/" + indexName + "/docs/search" + this.api_version
      log.info(s"[recommendation_engine]${searchCriterionTypeToString(stage)} url : $searchUrl")

      Try {
        Unirest.post(searchUrl).headers(this.headers.asJava).body(body).asString()
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
            throw new ServerErrorException(s"[recommendation_engine][exception]${searchCriterionTypeToString(stage)}Search request failed for ${body.replace("\n", "")}. Error message: $errorMessage")
          }

        case Failure(exception) =>
          throw new ServerErrorException(s"[recommendation_engine][exception]${searchCriterionTypeToString(stage)}Search request failed for ${body.replace("\n", "")}. Error message: ${exception.getMessage}")
      }

    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000000.0
      log.info(s"[recommendation_engine][execute_search]${searchCriterionTypeToString(stage)}[total_time] Search request: ${body.replace("\n", "")}: $diffTime")
    }
  }

  def updateEmployeeRecommendationWithNewInformation(selectedEmployeeListComingFromSearch: mutable.LinkedHashMap[String, EmployeeRecommendation],
                                                     employeeRecommendation: List[EmployeeRecommendation]): mutable.LinkedHashMap[String, EmployeeRecommendation] = {
    employeeRecommendation.foreach {
      retrievedCandidate =>
        val mail = retrievedCandidate.mailAddressFormatted()
        if (selectedEmployeeListComingFromSearch.contains(mail)) {
          val first = selectedEmployeeListComingFromSearch(mail)
          val updated = EmployeeRecommendation.merge(first, retrievedCandidate)
          selectedEmployeeListComingFromSearch.update(mail, updated)
        }
        else {
          selectedEmployeeListComingFromSearch.put(mail, retrievedCandidate)
        }
    }
    selectedEmployeeListComingFromSearch
  }

  def performSearchOnSendMails(requiredSkills: List[String],
                               indexName: String,
                               fieldName: String,
                               stage: SearchCriterionType,
                               searchSettings: SearchSettingsRequestResponse,
                               limit: Int = 2000,
                               requestedEmployeeAddresses: Set[String] = Set()): Map[String, Any] = {


    val startTime = System.nanoTime()

    try {

      val optimizedRequiredSkills = RecommendationEngineUtil.expandQueryList(requiredSkills)
      val facet = "From"

      //if phrase search is used (e.g. "data sciens") we have to escape the double quotes in the string.
      // See: https://docs.microsoft.com/en-us/azure/search/query-simple-syntax#keyword-search-on-terms-and-phrases
      val searchString = optimizedRequiredSkills.mkString(" ").replaceAll("\"", "\\\\\"")
      val filterParam = RecommendationEngineUtil.buildTimeFilterParam(searchSettings, requestedEmployeeAddresses, emailFilterField = "From")
      //
      val numberOfEmployees = requestedEmployeeAddresses.size
      val facetQueryParam = s"$facet,count:$limit"

      log.info(s"[recommendation_engine][$stage] search_string encoded: $searchString")

      val searchResultAsMapFromJson: Map[String, Any] =
        executeSearchWithPost(indexName = indexName,
          searchString = searchString,
          filterParam = filterParam,
          facetQueryParam = facetQueryParam,
          searchFields = fieldName,
          highlightFields = fieldName,
          top = limit,
          skip = 0,
          stage = stage)

      searchResultAsMapFromJson
    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000
      log.info(s"[recommendation_engine][performSearchOnMailsIndex][$stage][total_time]: $diffTime")
    }
  }

  def performSearchOnReceivedMails(requiredSkills: List[String],
                                   indexName: String,
                                   fieldName: String,
                                   stage: SearchCriterionType,
                                   searchSettings: SearchSettingsRequestResponse,
                                   limit: Int = 2000): Map[String, Any] = {


    val startTime = System.nanoTime()

    try {

      val optimizedRequiredSkills = RecommendationEngineUtil.expandQueryList(requiredSkills)

      val facet = "ToRecipients"
      val searchString = optimizedRequiredSkills.mkString(" ").replaceAll("\"", "\\\\\"")
      val filterParam = RecommendationEngineUtil.buildTimeFilterParam(searchSettings)
      val facetQueryParam = s"$facet,count:100"

      log.info(s"[recommendation_engine][$stage] search_string : $searchString")

      val searchResultAsMapFromJson: Map[String, Any] = //executeSearch(indexName, search_params, stage)
        executeSearchWithPost(indexName = indexName,
          searchString = searchString,
          filterParam = filterParam,
          facetQueryParam = facetQueryParam,
          searchFields = fieldName,
          highlightFields = fieldName,
          top = limit,
          skip = 0,
          stage = stage)

      searchResultAsMapFromJson
    }
    finally {
      val diffTime = (System.nanoTime() - startTime) / 1000000
      log.info(s"[recommendation_engine][performSearchOnMailsIndex][$stage][total_time]: $diffTime")
    }
  }

  def buildEmployeeRecommendationsFromEmailContent(searchResultAsMapFromJson: Map[String, Any],
                                                   stage: SearchCriterionType,
                                                   mailAddressesToUserProfileMap: Map[String, EmployeeRecommendation],
                                                   taxonomyList: List[TaxonomyType.Value] = List(TaxonomyType.SOFTWARE),
                                                   hierarchyOrderingScore: Double,
                                                   fieldName: String,
                                                   searchMappings: Boolean = false): (mutable.LinkedHashSet[String], mutable.Map[String, EmployeeRecommendation]) = {

    val facet = "From"

    val orderedEmployeesByVolume: mutable.LinkedHashSet[String] =
      searchResultAsMapFromJson.get("@search.facets").map {
        searchResults: Any =>
          val convertedSearchResults = searchResults.asInstanceOf[util.HashMap[String, Any]].asScala.toMap
          val targetUsers = convertedSearchResults(facet).asInstanceOf[util.ArrayList[util.HashMap[String, Any]]].asScala.toList

          val selectedUsers: immutable.Seq[String] = targetUsers.map(userCountMap => userCountMap.get("value").toString.trim.toLowerCase)
            .filter(employeeEmailAddress => mailAddressesToUserProfileMap.contains(employeeEmailAddress))

          mutable.LinkedHashSet(selectedUsers: _*)

      }.getOrElse(mutable.LinkedHashSet.empty[String])

    /**
     * We obtain a map of the users ordered by the volume of mail they received.
     * The map comes from the facets field.
     */

    val extractedResultsAsMapsFromJson: List[util.HashMap[String, Any]] = searchResultAsMapFromJson.get("value")
      .map {
        searchResults: Any =>
          val convertedSearchResults: mutable.Seq[util.HashMap[String, Any]] = searchResults.asInstanceOf[util.ArrayList[util.HashMap[String, Any]]].asScala
          log.info(s"[recommendation_engine][$stage] length search results : ${convertedSearchResults.length}")
          convertedSearchResults.toList
      }
      .getOrElse(Nil)
      .filter { nestedResult =>
        val mail = nestedResult.getOrDefault("TargetUser", "").toString.trim.toLowerCase
        //check is mail in the available profiles and check mail was not retrieved already in a previous step
        mailAddressesToUserProfileMap.contains(mail)
      }

    val retrievedEmployeeList: Seq[EmployeeRecommendation] = extractedResultsAsMapsFromJson.flatMap {
      nestedResult: util.HashMap[String, Any] =>
        recommendationService.buildEmployeeRecommendationFromMailSearch(mailAddressesToUserProfileMap,
          hierarchyOrderingScore,
          nestedResult, taxonomyList, searchMappings)
    }

    val mailToEmployeeRecommendationMap: mutable.Map[String, EmployeeRecommendation] = new mutable.LinkedHashMap[String, EmployeeRecommendation]
    val selectedUsersMailsFromSearch: mutable.Set[String] = new scala.collection.mutable.HashSet[String]()
    val finalEmployees = new scala.collection.mutable.ListBuffer[EmployeeRecommendation]()

    retrievedEmployeeList.foreach {
      scoredEmployee: EmployeeRecommendation =>
        if (!selectedUsersMailsFromSearch.contains(scoredEmployee.email.toLowerCase())) {
          recommendationService.explainEmployeeRecommendation(scoredEmployee, searchMappings, fieldName, stage)

          finalEmployees += scoredEmployee
          selectedUsersMailsFromSearch += scoredEmployee.email.toLowerCase
          mailToEmployeeRecommendationMap += (scoredEmployee.email.toLowerCase -> scoredEmployee)
          conditionalLogger.info(s"[recommendation_engine][$stage] result: $scoredEmployee")
        }
    }

    (orderedEmployeesByVolume, mailToEmployeeRecommendationMap)

  }

  def buildEmployeeRecommendationsFromReceivedEmailsContent(searchResultAsMapFromJson: Map[String, Any],
                                                            searchMappings: Boolean = false,
                                                            stage: SearchCriterionType,
                                                            mailAddressesToUserProfileMap: Map[String, EmployeeRecommendation],
                                                            hierarchyOrderingScore: Double,
                                                            taxonomyList: List[TaxonomyType.Value] = List(TaxonomyType.SOFTWARE),
                                                            fieldName: String): (mutable.LinkedHashSet[String], mutable.Map[String, EmployeeRecommendation]) = {

    val extractedResultsAsMapsFromJson: List[util.HashMap[String, Any]] = searchResultAsMapFromJson.get("value")
      .map {
        searchResults: Any =>
          val convertedSearchResults: mutable.Seq[util.HashMap[String, Any]] = searchResults.asInstanceOf[java.util.ArrayList[java.util.HashMap[String, Any]]].asScala
          log.info(s"[recommendation_engine][$stage] length search results : ${convertedSearchResults.length}")
          convertedSearchResults.toList
      }
      .getOrElse(Nil)
      .flatMap {
        nestedMap: util.HashMap[String, Any] =>
          val recipients = nestedMap.asScala("ToRecipients").toString.split("[,]").toList
          val finalRecipients: List[String] = recipients.map(p => p.toLowerCase().trim).filter {
            recipient =>
              mailAddressesToUserProfileMap.contains(recipient)
          }
          finalRecipients.map {
            recipient: String =>
              val newMap = new util.HashMap[String, Any](nestedMap)
              newMap.put("TargetUser", recipient)
              newMap
          }
      }
      .filter { nestedResult: util.HashMap[String, Any] =>
        //here filter for toRecipients
        val mail = nestedResult.getOrDefault("TargetUser", "").toString.trim.toLowerCase
        //check is mail in the available profiles and check mail was not retrieved already in a previous step
        mailAddressesToUserProfileMap.contains(mail)
      }

    val emailAddressFacet: mutable.LinkedHashSet[String] = {
      val sortedUsersByVol = extractedResultsAsMapsFromJson.map {
        nestedResults =>
          nestedResults.get("TargetUser").toString
      }.toList.groupBy(identity).map((p: (String, List[String])) => (p._1, p._2.length)).toList.sortBy((p: (String, Int)) => p._2).map(_._1).reverse

      mutable.LinkedHashSet(sortedUsersByVol: _*)
    }

    val retrievedEmployeeList: Seq[EmployeeRecommendation] = extractedResultsAsMapsFromJson.flatMap {
      nestedResult =>
        recommendationService.buildEmployeeRecommendationFromMailSearch(mailAddressesToUserProfileMap,
          hierarchyOrderingScore,
          nestedResult,
          taxonomyList = taxonomyList,
          searchMappings = searchMappings)
    }

    val mailToEmployeeRecommendationMap: mutable.Map[String, EmployeeRecommendation] = new mutable.LinkedHashMap[String, EmployeeRecommendation]
    val selectedUsersMailsFromSearch: mutable.Set[String] = new scala.collection.mutable.HashSet[String]()
    val finalEmployees = new scala.collection.mutable.ListBuffer[EmployeeRecommendation]()

    retrievedEmployeeList.foreach {
      scoredEmployee =>
        if (!selectedUsersMailsFromSearch.contains(scoredEmployee.email.toLowerCase())) {
          recommendationService.explainEmployeeRecommendation(scoredEmployee, searchMappings, fieldName, stage)

          finalEmployees += scoredEmployee
          selectedUsersMailsFromSearch += scoredEmployee.email.toLowerCase
          mailToEmployeeRecommendationMap += (scoredEmployee.email.toLowerCase -> scoredEmployee)
          conditionalLogger.info(s"[recommendation_engine][$stage] result: $scoredEmployee")

        }

    }

    (emailAddressFacet, mailToEmployeeRecommendationMap)
  }

}
