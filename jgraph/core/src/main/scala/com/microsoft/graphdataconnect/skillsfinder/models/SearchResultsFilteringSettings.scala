package com.microsoft.graphdataconnect.skillsfinder.models

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.SearchFiltersByDataSource

case class SearchResultsFilteringSettings(searchResultsFilters: List[SearchFiltersByDataSource])
