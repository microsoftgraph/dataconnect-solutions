/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable

import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.secret.Vault
import com.microsoft.graphdataconnect.skillsfinder.airtable.config.{ConfigArgs, Constants, GDCConfiguration, SparkInitializer}
import com.microsoft.graphdataconnect.skillsfinder.airtable.converters.AirtableBEToHRDataConverter
import com.microsoft.graphdataconnect.skillsfinder.airtable.model.be.AirtableEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.airtable.reader.EmployeeReader
import com.microsoft.graphdataconnect.skillsfinder.airtable.writer.EmployeeWriter
import org.apache.spark.sql.{Dataset, SparkSession}


object AirtableToHRData {
  private var log: GdcLogger = _

  def main(arg: Array[String]): Unit = {
    try {
      val config: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(arg)
      if (config.isDefined) {
        convert(config.get)
      } else {
        throw new IllegalArgumentException("Invalid command line arguments")
      }
    } catch {
      case e: Throwable =>
        if (log != null) {
          log.error(e.getMessage, e)
        } else {
          e.printStackTrace()
        }
        throw e
    }
  }

  def convert(configArgs: ConfigArgs): Unit = {
    implicit val sparkSession: SparkSession = SparkInitializer().session

    val vault = createVault(configArgs)

    implicit val configuration: GDCConfiguration = GDCConfiguration(configArgs, vault)
    configuration.setSparkSettings(sparkSession.sparkContext)

    log = configuration.getLogger(AirtableToHRData.getClass)

    if (isAirtableConfigured(configArgs, vault)) {
      val employeeReader = EmployeeReader(configArgs, vault)
      val employeesAirtable: Dataset[AirtableEmployeeDetails] = employeeReader.readAirtableData()

      import sparkSession.implicits._
      val employeesHRData: Dataset[HRDataEmployeeDetails] = employeesAirtable.map { airtableEmployeeDetails =>
        AirtableBEToHRDataConverter.convertEmployee(airtableEmployeeDetails)
      }

      val employeeWriter: EmployeeWriter = EmployeeWriter()
      employeeWriter.write(employeesHRData)
      log.info("Finished converting Airtable information")
    } else {
      log.info("Airtable keys are not configured in KeyVault. Skipping converting Airtable data into HR data")
    }
  }

  private def createVault(configArgs: ConfigArgs): Vault = {
    if (configArgs.isDevMode) {
      Vault(Map(
        configArgs.airtableBaseKeyName -> sys.env.getOrElse(Constants.AIRTABLE_BASE_KEY, ""),
        configArgs.airtableApiKeyName -> sys.env.getOrElse(Constants.AIRTABLE_API_KEY, "")
      ))
    } else {
      Vault(keyVaultUrl = configArgs.keyVaultUrl.get,
        clientId = configArgs.applicationId.get,
        tenantId = configArgs.directoryId.get,
        adbSecretScopeName = configArgs.adbSecretScopeName,
        adbServicePrincipalKeySecretName = configArgs.adbSPClientKeySecretName)
    }
  }

  private def isAirtableConfigured(configArgs: ConfigArgs, vault: Vault): Boolean = {
    try {
      // If the secrets required to connect to Airtable were not defined in Azure KeyVault, the following two
      // calls should throw an exception. In that case this job should not write anything to the output location
      vault.getSecretValue(configArgs.airtableBaseKeyName)
      vault.getSecretValue(configArgs.airtableApiKeyName)
      true
    } catch {
      case _: Throwable => false
    }
  }

}