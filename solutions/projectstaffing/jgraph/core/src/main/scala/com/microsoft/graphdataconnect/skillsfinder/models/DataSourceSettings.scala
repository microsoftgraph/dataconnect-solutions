package com.microsoft.graphdataconnect.skillsfinder.models

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings
import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.{DataSource, DataSourceType}

case class DataSourceSettings(dataSourcesPriority: List[DataSourceType],
                              isHRDataMandatory: Boolean) {
  def toDataSources(): List[DataSource] = {
    dataSourcesPriority.map { dataSourceType =>
      val isMandatory = dataSourceType match {
        case DataSourceType.M365 => true
        case DataSourceType.HRData => isHRDataMandatory
        case _ => false
      }
      settings.DataSource(dataSourceType, isMandatory = isMandatory)
    }
  }
}

object DataSourceSettings {
  def apply(dataSources: List[DataSource]): DataSourceSettings = {
    val dataSourcesPriority = dataSources.map(_.dataSourceType)
    val isHRDataMandatory = dataSources.find(_.dataSourceType.equals(DataSourceType.HRData))
      .map(_.isMandatory)
      .getOrElse(false)
    DataSourceSettings(dataSourcesPriority, isHRDataMandatory)
  }
}
