/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.reader

import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.configs.IngestionMode
import com.microsoft.graphdataconnect.secret.Vault
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.access.AirtableReader
import com.microsoft.graphdataconnect.skillsfinder.airtable.config.{ConfigArgs, GDCConfiguration}
import com.microsoft.graphdataconnect.skillsfinder.airtable.model.be.AirtableEmployeeDetails
import org.apache.spark.sql.{Dataset, SparkSession}

trait EmployeeReader {

  var log: GdcLogger = _

  def readAirtableData(): Dataset[AirtableEmployeeDetails]

}


object EmployeeReader {

  def apply(configArgs: ConfigArgs, vault: Vault)
           (implicit sparkSession: SparkSession, configuration: GDCConfiguration): EmployeeReader = {

    configArgs.ingestionMode match {
      case IngestionMode.Sample =>
        new EmployeeReaderFromStorage()

      case IngestionMode.Production =>
        val airtableReader: AirtableReader = AirtableReader(vault.getSecretValue(configArgs.airtableBaseKeyName), vault.getSecretValue(configArgs.airtableApiKeyName))
        new EmployeeReaderFromAirtable(airtableReader)

      case IngestionMode.Simulated =>
        new EmployeeReaderFromStorage()

      case _ => throw new Exception("Unknown Ingestion mode")
    }
  }

}
