package com.microsoft.graphdataconnect.skillsfinder.models

import java.time.OffsetDateTime

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.{SearchCriterion, SearchFiltersByDataSource, SearchSettings}

object SearchSettingsRequestResponse {
  def fromSearchSettings(searchSettings: SearchSettings): SearchSettingsRequestResponse = {

    SearchSettingsRequestResponse(
      dataSourceSettings = DataSourceSettings(searchSettings.dataSources),
      searchCriteria = searchSettings.searchCriteria,
      searchResultsFilters = searchSettings.searchResultsFilters,
      useReceivedEmailsContent = searchSettings.useReceivedEmailsContent,
      freshness = searchSettings.freshness,
      freshnessEnabled = searchSettings.freshnessEnabled,
      volume = searchSettings.volume,
      volumeEnabled = searchSettings.volumeEnabled,
      relevanceScore = searchSettings.relevanceScore,
      relevanceScoreEnabled = searchSettings.relevanceScoreEnabled,
      freshnessBeginDate = searchSettings.freshnessBeginDate,
      freshnessBeginDateEnabled = searchSettings.freshnessBeginDateEnabled,
      includedEmailDomains = searchSettings.includedEmailDomains,
      includedEmailDomainsEnabled = searchSettings.includedEmailDomainsEnabled,
      excludedEmailDomains = searchSettings.excludedEmailDomains,
      excludedEmailDomainsEnabled = searchSettings.excludedEmailDomainsEnabled
    )
  }

}

//TODO: change the name. Remove Response from this class
case class SearchSettingsRequestResponse(dataSourceSettings: DataSourceSettings,
                                         searchCriteria: List[SearchCriterion],
                                         searchResultsFilters: List[SearchFiltersByDataSource], // = List(SearchFiltersByDataSource(dataSource = HRData, filters = List(SearchFilterConfig("hr_data_locate", isActive = true)))),
                                         useReceivedEmailsContent: Boolean,
                                         freshness: Integer,
                                         freshnessEnabled: Boolean,
                                         volume: Integer,
                                         volumeEnabled: Boolean,
                                         relevanceScore: Integer,
                                         relevanceScoreEnabled: Boolean,
                                         freshnessBeginDate: OffsetDateTime,
                                         freshnessBeginDateEnabled: Boolean,
                                         includedEmailDomains: List[String],
                                         includedEmailDomainsEnabled: Boolean,
                                         excludedEmailDomains: List[String],
                                         excludedEmailDomainsEnabled: Boolean,
                                         taxonomyList: List[TaxonomyType.Value] = TaxonomyType.values.toList) {
  def updateTaxonomyList(taxonomyList: List[String]): SearchSettingsRequestResponse = {
    val updatedTaxonomyList = taxonomyList.map { taxonomyAsString => TaxonomyType.from(taxonomyAsString) }
    new SearchSettingsRequestResponse(dataSourceSettings,
      searchCriteria,
      searchResultsFilters,
      useReceivedEmailsContent,
      freshness,
      freshnessEnabled,
      volume,
      volumeEnabled,
      relevanceScore,
      relevanceScoreEnabled,
      freshnessBeginDate,
      freshnessBeginDateEnabled,
      includedEmailDomains,
      includedEmailDomainsEnabled,
      excludedEmailDomains,
      excludedEmailDomainsEnabled,
      taxonomyList = updatedTaxonomyList)
  }

}

