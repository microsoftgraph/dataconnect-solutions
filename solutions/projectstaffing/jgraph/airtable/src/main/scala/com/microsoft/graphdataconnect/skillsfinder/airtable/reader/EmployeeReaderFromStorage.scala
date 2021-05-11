/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.reader

import com.microsoft.graphdataconnect.skillsfinder.airtable.config.GDCConfiguration
import com.microsoft.graphdataconnect.skillsfinder.airtable.model.be.AirtableEmployeeDetails
import org.apache.spark.sql.{Dataset, SparkSession}

protected class EmployeeReaderFromStorage()
                                         (implicit sparkSession: SparkSession,
                                          configuration: GDCConfiguration) extends EmployeeReader {


  override def readAirtableData(): Dataset[AirtableEmployeeDetails] = {
    log = configuration.getLogger(classOf[EmployeeReaderFromStorage])

    val airtableDataFullPath: String = configuration.getAirtableDataFullPath

    import sparkSession.implicits._

    log.info(s"Reading airtable data from path: $airtableDataFullPath")
    val airtableData = sparkSession.read.option("header", value = true).csv(airtableDataFullPath)
    val airtableUserData: Dataset[AirtableEmployeeDetails] = airtableData.as[AirtableEmployeeDetails]

    log.info(s"Successfully read airtable data")

    airtableUserData
  }
}

