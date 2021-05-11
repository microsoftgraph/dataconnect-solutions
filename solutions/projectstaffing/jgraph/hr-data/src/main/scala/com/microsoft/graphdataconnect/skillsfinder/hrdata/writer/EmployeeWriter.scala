/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.hrdata.writer

import com.microsoft.graphdataconnect.jdbc.JdbcClient
import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.hrdata.config.GDCConfiguration
import org.apache.spark.sql.{Dataset, SparkSession}

trait EmployeeWriter {
  def write(hrDataEmployeeDataset: Dataset[HRDataEmployeeDetails]): Unit
}

object EmployeeWriter {
  def apply()
           (implicit sparkSession: SparkSession, configuration: GDCConfiguration): EmployeeWriter = {
    val log: GdcLogger = configuration.getLogger(EmployeeWriter.getClass)

    val jdbcUrl: String = configuration.getJdbcUrl()
    val jdbcClient: JdbcClient = JdbcClient(configuration.getDatabaseConnectionProperties(), jdbcUrl)(log)

    new EmployeeWriterToDB(jdbcClient, jdbcUrl, configuration.getDatabaseConnectionProperties(), configuration.getMaxDbConnections)
  }
}