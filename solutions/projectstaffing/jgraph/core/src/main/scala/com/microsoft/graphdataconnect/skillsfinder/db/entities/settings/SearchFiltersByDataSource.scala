package com.microsoft.graphdataconnect.skillsfinder.db.entities.settings

case class SearchFiltersByDataSource(dataSource: DataSourceType, filters: Seq[SearchFilterConfig])
