package com.microsoft.graphdataconnect.skillsfinder.models.business.employee

import com.microsoft.graphdataconnect.skillsfinder.models.business.RequiredAvailability
import com.microsoft.graphdataconnect.skillsfinder.models.{FilterType, OrderingType}

case class EmployeeSearchQuery(searchTerms: List[String],
                               searchCriteria: EmployeeSearchCriteria = EmployeeSearchCriteria.SKILLS,
                               requiredAvailability: RequiredAvailability,
                               opportunityId: Long,
                               taxonomiesList: List[String],
                               sortBy: OrderingType,
                               searchFilterValues: Map[FilterType, List[String]] = Map.empty)
