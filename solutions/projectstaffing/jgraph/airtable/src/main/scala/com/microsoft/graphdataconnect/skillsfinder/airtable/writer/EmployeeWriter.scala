/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.writer

import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.airtable.config.GDCConfiguration
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}


class EmployeeWriter()(implicit sparkSession: SparkSession, implicit val configuration: GDCConfiguration) {
  var log: GdcLogger = _

  def write(hrDataEmployeeDataset: Dataset[HRDataEmployeeDetails]): Unit = {
    log = configuration.getLogger(classOf[EmployeeWriter])
    val outputPath = configuration.getOutputHRDataFullPath

    log.info(s"Writing new HR Data derived from Airtable in $outputPath")
    hrDataEmployeeDataset.write
      .option("header", true)
      .mode(SaveMode.Overwrite)
      .csv(outputPath)

    log.info(s"Successfully wrote new HR Data")
  }

}

object EmployeeWriter {
  def apply()(implicit sparkSession: SparkSession, configuration: GDCConfiguration): EmployeeWriter = {
    new EmployeeWriter()
  }
}
