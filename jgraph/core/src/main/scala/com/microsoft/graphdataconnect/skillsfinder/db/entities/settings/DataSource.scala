package com.microsoft.graphdataconnect.skillsfinder.db.entities.settings

case class DataSource(dataSourceType: DataSourceType,
                      isEnabled: Boolean = true,
                      isMandatory: Boolean = true)


