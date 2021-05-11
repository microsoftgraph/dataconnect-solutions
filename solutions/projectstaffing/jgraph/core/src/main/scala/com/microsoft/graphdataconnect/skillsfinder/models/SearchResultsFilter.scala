/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.DataSourceType
import com.microsoft.graphdataconnect.skillsfinder.models.FilterType._

import scala.collection.mutable

object SearchResultsFilter extends Enumeration {
  type SearchResultsFilter = SearchResultsFilterValue

  // TODO keep the value of azSearchField in sync with the field name in azure_employees_index_definition.json,
  //  keep the value of dbTable and dbColumn in sync with the latest db schema of the employee_profile and hr_data_employee_profile tables
  case class SearchResultsFilterValue(filterType: FilterType, dataSource: DataSourceType, azSearchFacetField: String, dbTable: String, dbColumn: String) extends Val(filterType.toString)

  val m365Country = SearchResultsFilterValue(M365_COUNTRY, DataSourceType.M365, "country", "employee_profile", "country")
  val m365State = SearchResultsFilterValue(M365_STATE, DataSourceType.M365, "state", "employee_profile", "state")
  val m365City = SearchResultsFilterValue(M365_CITY, DataSourceType.M365, "city", "employee_profile", "city")
  //  val m365OfficeLocation = SearchResultsFilterValue(M365_OFFICE_LOCATION, DataSourceType.M365, "office_location", "employee_profile", "office_location")
  //  val m365CompanyName = SearchResultsFilterValue(M365_COMPANY_NAME, DataSourceType.M365, "company_name", "employee_profile", "company_name")
  val m365Department = SearchResultsFilterValue(M365_DEPARTMENT, DataSourceType.M365, "department", "employee_profile", "department")
  val m365Role = SearchResultsFilterValue(M365_ROLE, DataSourceType.M365, "job_title", "employee_profile", "job_title")
  val hrDataLocation = SearchResultsFilterValue(HR_DATA_LOCATION, DataSourceType.HRData, "hr_data_location", "hr_data_employee_profile", "location")
  val hrDataRole = SearchResultsFilterValue(HR_DATA_ROLE, DataSourceType.HRData, "hr_data_role", "hr_data_employee_profile", "role")

  lazy val valueSet: mutable.Set[SearchResultsFilter] = {
    val result = new mutable.LinkedHashSet[SearchResultsFilter]()
    values.toSeq.map(entry => entry.asInstanceOf[SearchResultsFilter]).foreach(value => result.add(value))
    result
  }

  private lazy val valuesByFilterType: Map[FilterType, SearchResultsFilter] = valueSet.map(entry => entry.filterType -> entry).toMap

  private lazy val valuesByAzSearchFacetField: Map[String, SearchResultsFilter] = valueSet.map(entry => entry.azSearchFacetField -> entry).toMap

  def fromType(filterType: String): SearchResultsFilter = {
    valueSet.find(_.toString.equals(filterType))
      .getOrElse(throw new IllegalArgumentException(s"Invalid filter type $filterType"))
  }

  def fromType(filterType: FilterType): SearchResultsFilter = {
    valuesByFilterType.getOrElse(filterType, throw new IllegalArgumentException(s"Invalid filter type $filterType"))
  }

  def fromAzSearchFacetField(facetField: String): SearchResultsFilter = {
    valuesByAzSearchFacetField.getOrElse(facetField, throw new IllegalArgumentException(s"Invalid AzureSearch index facet field $facetField"))
  }

  def filtersByDataSource(dataSourceType: DataSourceType): Seq[SearchResultsFilter] = {
    valueSet.toSeq.filter(_.dataSource == dataSourceType)
  }


}
