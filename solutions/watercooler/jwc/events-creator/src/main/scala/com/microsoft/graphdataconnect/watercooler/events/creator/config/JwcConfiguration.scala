/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator.config

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Properties

import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLoggerFactory
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.microsoft.graphdataconnect.watercooler.common.db.DatabaseConnectionPropertiesFactory
import com.microsoft.graphdataconnect.watercooler.common.logging.{JwcLogger, JwcLoggerFactory}
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import com.microsoft.graphdataconnect.watercooler.common.util.{LogAnalyticsUtils, TimeUtils}
import pureconfig.ConfigSource

import scala.util.Try

/*
 * hardcoded values
 */
abstract class JwcConfiguration() {

  def getDatabaseConnectionProperties(): Properties

  val emailDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  def getLogger(clazz: Class[_]): JwcLogger

  def getLoggerWorkspaceId(): String

  def getLogType(): String

  def getLoggerWorkspaceKey(): String

  def getGraphApiAppAccessInfo(): GraphApiAppAccessInfo

  def getMeetingOrganizerEmail(): String

  def getStartDate(): LocalDate

  def getEndDate(): LocalDate

  def getMeetingDuration(): Int

  def getJdbcUrl(): String
}

class LocalConfiguration(applicationId: String,
                         applicationSecret: String,
                         directoryId: String,
                         jdbcHostname: String,
                         jdbcPort: String,
                         jdbcDatabase: String,
                         jdbcUsername: String,
                         jdbcPassword: String,
                         meetingOrganizerEmail: String,
                         startDate: String,
                         endDate: String,
                         meetingDuration: String
                        ) extends JwcConfiguration {

  override def getDatabaseConnectionProperties(): Properties = {
    val connectionProperties = new Properties()
    connectionProperties.setProperty("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    connectionProperties
  }

  override def getLogger(clazz: Class[_]): JwcLogger = JwcLoggerFactory.getLogger(clazz = clazz)

  override def getLoggerWorkspaceId(): String = ""

  override def getLogType(): String = ""

  override def getLoggerWorkspaceKey(): String = ""

  override def getGraphApiAppAccessInfo(): GraphApiAppAccessInfo = GraphApiAppAccessInfo(applicationId, applicationSecret, directoryId)

  override def getMeetingOrganizerEmail(): String = meetingOrganizerEmail

  override def getStartDate(): LocalDate = Try(TimeUtils.stringToDate(startDate)).getOrElse(LocalDate.now())

  override def getEndDate(): LocalDate = Try(TimeUtils.stringToDate(endDate)).getOrElse(LocalDate.now())

  def getMeetingDuration(): Int = Try(meetingDuration.toInt).getOrElse(15)

  override def getJdbcUrl(): String = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase"

}

class DatabricksConfiguration(applicationId: String,
                              adbSPClientKeySecretName: String,
                              secretScope: String,
                              directoryId: String,
                              jdbcHostname: String,
                              jdbcPort: String,
                              jdbcDatabase: String,
                              jdbcUsernameKeyName: String,
                              jdbcPasswordKeyName: String,
                              useMsiAzureSqlAuth: String,
                              keyVaultUrl: String,
                              workspaceKey: String,
                              workspaceId: String,
                              meetingOrganizerEmail: String,
                              startDate: String,
                              endDate: String,
                              meetingDuration: String
                             ) extends JwcConfiguration {

  val logAnalyticsLogType = "EventsCreator"

  override def getDatabaseConnectionProperties(): Properties = {
    DatabaseConnectionPropertiesFactory.getProperties(
      applicationId, directoryId, secretScope, adbSPClientKeySecretName,
      jdbcUsernameKeyName, jdbcPasswordKeyName, "true".equals(useMsiAzureSqlAuth.toLowerCase().trim), keyVaultUrl)
  }

  override def getLogger(clazz: Class[_]): JwcLogger = JwcLoggerFactory.getLogger(logAnalyticsWorkspaceId = workspaceId,
    logAnalyticsSharedKey = workspaceKey,
    clazz = clazz,
    logType = logAnalyticsLogType
  )

  override def getLoggerWorkspaceId(): String = workspaceKey

  override def getLogType(): String = logAnalyticsLogType

  override def getLoggerWorkspaceKey(): String = workspaceKey

  override def getGraphApiAppAccessInfo(): GraphApiAppAccessInfo = GraphApiAppAccessInfo(applicationId, dbutils.secrets.get(scope = secretScope, key = adbSPClientKeySecretName), directoryId)

  override def getMeetingOrganizerEmail(): String = meetingOrganizerEmail

  override def getStartDate(): LocalDate = Try(TimeUtils.stringToDate(startDate)).getOrElse(LocalDate.now())

  override def getEndDate(): LocalDate = Try(TimeUtils.stringToDate(endDate)).getOrElse(LocalDate.now())

  def getMeetingDuration(): Int = Try(meetingDuration.toInt).getOrElse(15)

  override def getJdbcUrl(): String = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

}

class LocalAzureConfiguration(applicationId: String,
                              applicationSecret: String,
                              directoryId: String,
                              jdbcHostname: String,
                              jdbcPort: String,
                              jdbcDatabase: String,
                              jdbcUsername: String,
                              jdbcPassword: String,
                              meetingOrganizerEmail: String,
                              startDate: String,
                              endDate: String,
                              meetingDuration: String
                        ) extends JwcConfiguration {

  val logAnalyticsLogType = "MailsExtractor"

  override def getLogger(clazz: Class[_]): JwcLogger = JwcLoggerFactory.getLogger(clazz = clazz)

  override def getLoggerWorkspaceId(): String = ""

  override def getLogType(): String = ""

  override def getLoggerWorkspaceKey(): String = ""

  override def getDatabaseConnectionProperties(): Properties = {
    val connectionProperties = new Properties()
    connectionProperties.setProperty("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    connectionProperties
  }

  override def getGraphApiAppAccessInfo(): GraphApiAppAccessInfo = GraphApiAppAccessInfo(applicationId, applicationSecret, directoryId)

  override def getMeetingOrganizerEmail(): String = meetingOrganizerEmail

  override def getStartDate(): LocalDate = Try(TimeUtils.stringToDate(startDate)).getOrElse(LocalDate.now())

  override def getEndDate(): LocalDate = Try(TimeUtils.stringToDate(endDate)).getOrElse(LocalDate.now())

  def getMeetingDuration(): Int = Try(meetingDuration.toInt).getOrElse(15)

  override def getJdbcUrl(): String = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

}

object JwcConfiguration {

  val groupsStartEndDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def apply(args: Array[String]): JwcConfiguration = {
    if (args.contains("--dev")) {
      import pureconfig.generic.auto._    // needed for the implicits
      val config: ConfigFile = ConfigSource.resources("local-dev.conf").loadOrThrow[ConfigFile]

      new LocalAzureConfiguration(
        config.applicationId,
        config.applicationSecret,
        config.directoryId,
        config.jdbcHostname,
        config.jdbcPort,
        config.jdbcDatabase,
        config.jdbcUsername,
        config.jdbcPassword,
        config.meetingOrganizerEmail,
        config.startDate,
        config.endDate,
        config.meetingDuration
      )

    } else if(args.contains("--local")) {
      import pureconfig.generic.auto._    // needed for the implicits
      val config: ConfigFile = ConfigSource.resources("local-dev.conf").loadOrThrow[ConfigFile]

      new LocalConfiguration(
        config.applicationId,
        config.applicationSecret,
        config.directoryId,
        config.jdbcHostname,
        config.jdbcPort,
        config.jdbcDatabase,
        config.jdbcUsername,
        config.jdbcPassword,
        config.meetingOrganizerEmail,
        config.startDate,
        config.endDate,
        config.meetingDuration
      )

    } else {
      val configOpt: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(args.toArray)
      if(configOpt.isDefined) {
        val config: ConfigArgs = configOpt.get

        val (logAnalyticsWorkspaceId, logAnalyticsWorkspaceKey) = LogAnalyticsUtils.buildLogAnalyticsWorkspaceIdAndWorkspaceKey(
          workspaceId = config.logAnalyticsWorkspaceId,
          workspaceKey = config.logAnalyticsWorkspaceKeyName,
          applicationId = config.applicationId,
          directoryId = config.directoryId,
          adbSecretScopeName = config.adbSecretScopeName,
          adbSPClientKeySecretName = config.applicationSecretKeyName,
          keyVaultUrl = config.keyVaultUrl
        )

        new DatabricksConfiguration(
          config.applicationId,
          config.applicationSecretKeyName,
          config.adbSecretScopeName,
          config.directoryId,
          config.jdbcHostname,
          config.jdbcPort,
          config.jdbcDatabase,
          config.jdbcUsernameKeyName,
          config.jdbcPasswordKeyName,
          config.useMsiAzureSqlAuth,
          config.keyVaultUrl,
          logAnalyticsWorkspaceKey,
          logAnalyticsWorkspaceId,
          config.meetingOrganizerEmail,
          config.startDate,
          config.endDate,
          config.meetingDuration
        )

      } else {
        throw new IllegalArgumentException("Invalid command line arguments")
      }

    }
  }
}
