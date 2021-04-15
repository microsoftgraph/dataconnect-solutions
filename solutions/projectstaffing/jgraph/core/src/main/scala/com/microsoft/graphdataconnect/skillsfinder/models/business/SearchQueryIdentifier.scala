package com.microsoft.graphdataconnect.skillsfinder.models.business

import com.microsoft.graphdataconnect.skillsfinder.models.FilterType
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.EmployeeSearchQuery

case class SearchQueryIdentifier(searchTerms: List[String],
                                 requiredAvailability: RequiredAvailability,
                                 opportunityId: Long,
                                 taxonomiesList: List[String],
                                 searchFilterValues: Map[FilterType, List[String]] = Map.empty,
                                 top: Int,
                                 skip: Int,
                                 userId: String)


object SearchQueryIdentifier {
  def from(employeeSearchQuery: EmployeeSearchQuery, top: Int, skip: Int, userId: String): SearchQueryIdentifier = {
    SearchQueryIdentifier(searchTerms = employeeSearchQuery.searchTerms,
      requiredAvailability = employeeSearchQuery.requiredAvailability,
      opportunityId = employeeSearchQuery.opportunityId,
      taxonomiesList = employeeSearchQuery.taxonomiesList,
      searchFilterValues = employeeSearchQuery.searchFilterValues,
      top = top,
      skip = skip,
      userId = userId
    )
  }
}
