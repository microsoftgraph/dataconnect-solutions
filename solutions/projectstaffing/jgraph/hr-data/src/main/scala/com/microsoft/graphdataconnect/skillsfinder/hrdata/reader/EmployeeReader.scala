package com.microsoft.graphdataconnect.skillsfinder.hrdata.reader

import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.configs.IngestionMode
import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.hrdata.config.{ConfigArgs, GDCConfiguration}
import org.apache.spark.sql.{Dataset, SparkSession}

trait EmployeeReader {

  var log: GdcLogger = _

  def read(): Dataset[HRDataEmployeeDetails]

}


object EmployeeReader {

  def apply(configArgs: ConfigArgs)
           (implicit sparkSession: SparkSession, configuration: GDCConfiguration): EmployeeReader = {

    configArgs.ingestionMode match {
      case IngestionMode.Sample =>
        new EmployeeReaderFromStorage()

      case IngestionMode.Production =>
        new EmployeeReaderFromStorage()

      case IngestionMode.Simulated =>
        new EmployeeReaderFromStorage()

      case _ => throw new Exception("Unknown Ingestion mode")
    }
  }

}
