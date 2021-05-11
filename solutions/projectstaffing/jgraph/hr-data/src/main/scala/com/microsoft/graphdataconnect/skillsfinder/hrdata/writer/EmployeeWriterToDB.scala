/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.hrdata.writer

import java.time.{LocalDateTime, ZoneOffset}
import java.util.Properties

import com.microsoft.graphdataconnect.jdbc.JdbcClient
import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.configs.ConfigurationTypes
import com.microsoft.graphdataconnect.model.userdetails.db.HRDataEmployeeProfile
import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.hrdata.config.{Constants, GDCConfiguration}
import com.microsoft.graphdataconnect.skillsfinder.hrdata.converters.SourceToDBConverter
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}

class EmployeeWriterToDB(jdbcClient: JdbcClient,
                         jdbcUrl: String,
                         connectionProperties: Properties,
                         maxDbConnections: Int)
                        (implicit sparkSession: SparkSession, implicit val configuration: GDCConfiguration) extends EmployeeWriter {
  var log: GdcLogger = _

  override def write(hrDataEmployeeDataset: Dataset[HRDataEmployeeDetails]): Unit = {
    log = configuration.getLogger(classOf[EmployeeWriterToDB])

    val hrDataEmployeeProfileVersion: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
    val versionStr = TimeUtils.localDateTimeToString(hrDataEmployeeProfileVersion)

    import sparkSession.implicits._

    val hrDataEmployeeProfilesDS: Dataset[HRDataEmployeeProfile] = hrDataEmployeeDataset.map((employeeDetails: HRDataEmployeeDetails) => {
      SourceToDBConverter.convertEmployee(employeeDetails, versionStr)
    })

    log.info("Writing HR Data employee profiles into the database")
    hrDataEmployeeProfilesDS.coalesce(maxDbConnections) // Trying to keep the number of connections to the database small
      .write.mode(SaveMode.Append)
      .jdbc(jdbcUrl, Constants.HR_DATA_EMPLOYEE_TABLE_NAME, connectionProperties)

    log.info(s"Setting new HR Data employee profiles version into the database: $hrDataEmployeeProfileVersion")
    val versionUpdatedSuccessfully = jdbcClient.updateEmployeeDataVersion(ConfigurationTypes.LatestVersionOfHRDataEmployeeProfile, newVersion = hrDataEmployeeProfileVersion)

    if (versionUpdatedSuccessfully) {
      log.info("Successfully stored new employee profiles version")
      val waitDurationSeconds = configuration.getOldDataCleanupDelaySeconds()
      log.info(s"Waiting $waitDurationSeconds seconds to allow the application to finish any operations that still use the old data")
      Thread.sleep(waitDurationSeconds * 1000)
      log.info("Deleting old data")
      jdbcClient.deleteOldVersions(Constants.HR_DATA_EMPLOYEE_TABLE_NAME, olderThan = hrDataEmployeeProfileVersion)
      log.info("Done")
    } else {
      // throw exception and let ADF handle it
      throw new Exception(s"Could not set new version [$hrDataEmployeeProfileVersion] for HRData employee profiles")
    }
  }

}
