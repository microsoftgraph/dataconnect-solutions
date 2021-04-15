package com.microsoft.graphdataconnect.skillsfinder.db.entities.settings

import com.microsoft.graphdataconnect.skillsfinder.models.FilterType

case class SearchFilterConfig(filterType: FilterType, isActive: Boolean)
