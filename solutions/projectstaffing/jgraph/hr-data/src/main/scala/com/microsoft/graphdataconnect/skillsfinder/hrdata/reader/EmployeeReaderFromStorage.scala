/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.hrdata.reader

import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.hrdata.config.GDCConfiguration
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

protected class EmployeeReaderFromStorage()
                                         (implicit sparkSession: SparkSession,
                                          configuration: GDCConfiguration) extends EmployeeReader {


  override def read(): Dataset[HRDataEmployeeDetails] = {
    log = configuration.getLogger(classOf[EmployeeReaderFromStorage])

    val hrDataFullPath: String = configuration.getHRDataFullPath

    import sparkSession.implicits._

    val corruptDataHandlingMode = if (configuration.failFastOnCorruptData()) "FAILFAST" else "DROPMALFORMED"

    log.info(s"Reading HR Data from path: $hrDataFullPath")
    val hrData: DataFrame = sparkSession.read
      .option("header", value = true)
      .option("ignoreLeadingWhiteSpace", true)
      .option("ignoreTrailingWhiteSpace", true)
      .option("mode", corruptDataHandlingMode)
      .csv(hrDataFullPath)

    val hrDataEmployeeDetails: Dataset[HRDataEmployeeDetails] = hrData
      .as[HRDataEmployeeDetails]

    log.info(s"Successfully read HR Data")

    hrDataEmployeeDetails
  }
}

