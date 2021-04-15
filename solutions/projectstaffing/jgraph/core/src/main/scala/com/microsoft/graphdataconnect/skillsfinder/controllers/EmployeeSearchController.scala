package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.skillsfinder.exceptions.{AzureSearchIndexConfigurationMissing, AzureSearchIndexNotFound}
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.{EmployeeResponseWrapper, EmployeeSearchQuery, SkillSuggestion}
import com.microsoft.graphdataconnect.skillsfinder.models.{SearchFilterValues, TaxonomyType}
import com.microsoft.graphdataconnect.skillsfinder.service.EmployeeSearchService
import com.microsoft.graphdataconnect.skillsfinder.service.search.SearchTermExpansionService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

import scala.util.{Failure, Success, Try}

@Controller
@RequestMapping(Array("/gdc/employee-search"))
class EmployeeSearchController(@Autowired employeeSearchService: EmployeeSearchService,
                               @Autowired searchTermExpansionService: SearchTermExpansionService) {

  private val logger: Logger = LoggerFactory.getLogger(classOf[EmployeeSearchController])

  @PostMapping(Array("/search"))
  def getEmployeeRecommendations(@RequestBody employeeSearchQuery: EmployeeSearchQuery,
                                 @RequestParam(name = "size", required = false, defaultValue = "50") size: Integer,
                                 @RequestParam(name = "offset", required = false, defaultValue = "0") offset: Integer,
                                 @RequestAttribute("userId") userId: String): ResponseEntity[EmployeeResponseWrapper] = {
    logger.info(s"Asked for Employee Recommendation using query: $employeeSearchQuery, size: $size, offset: $offset")
    val recommendedEmployees = Try {
      employeeSearchService.getRecommendedEmployees(employeeSearchQuery, size, offset, userId)
    } match {
      case Success(responseWrapper: EmployeeResponseWrapper) => responseWrapper
      //      case Failure(_: AzureSearchIndexNotFound) => EmployeeResponseWrapper(0, List())
      //      case Failure(_: AzureSearchIndexConfigurationMissing) => EmployeeResponseWrapper(0, List())
      case Failure(e: Throwable) => throw e
    }
    ResponseEntity.status(HttpStatus.OK).body(recommendedEmployees)
  }

  @GetMapping(Array("/skill-suggestions"))
  def getEmployeeSkillSuggestions(@RequestParam beginsWith: String, @RequestParam taxonomiesList: String): ResponseEntity[Seq[SkillSuggestion]] = {
    val suggestions = Try {
      val taxoList = taxonomiesList.split("[,]").filter(p => p.trim.nonEmpty).map { p => TaxonomyType.from(p) }.toList
      searchTermExpansionService.getRecommendedSkillsWithRelatedTerms(beginsWith, taxoList)
    } match {
      case Success(suggestions: Seq[SkillSuggestion]) => suggestions
      case Failure(_: AzureSearchIndexNotFound) => List()
      case Failure(_: AzureSearchIndexConfigurationMissing) => List()
      case Failure(e: Throwable) => throw e
    }
    ResponseEntity.status(HttpStatus.OK).body(suggestions)
  }

  @GetMapping(Array("/search-filters"))
  def getFacets(@RequestAttribute("userId") userId: String): ResponseEntity[Seq[SearchFilterValues]] = {
    val facets: Seq[SearchFilterValues] = Try {
      employeeSearchService.getFacets(userId)
    } match {
      case Success(result: Seq[SearchFilterValues]) => result
      case Failure(_: AzureSearchIndexNotFound) => List.empty
      case Failure(_: AzureSearchIndexConfigurationMissing) => List.empty
      case Failure(e: Throwable) => throw e
    }
    ResponseEntity.status(HttpStatus.OK).body(facets)
  }

}
