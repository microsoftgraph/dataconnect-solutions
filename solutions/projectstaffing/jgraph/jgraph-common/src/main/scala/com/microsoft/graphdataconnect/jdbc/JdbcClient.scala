/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.jdbc

import java.sql.{Connection, ResultSet}
import java.time.LocalDateTime
import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.configs.{AzureSearchEmployeesIndexConfiguration, ConfigurationTypes}
import com.microsoft.graphdataconnect.utils.TimeUtils
import com.microsoft.sqlserver.jdbc.SQLServerDataSource

import scala.util.control.NonFatal

trait JdbcClient {

  val log: GdcLogger

  // TODO add a connection pool manager [eg: c3p0]
  val connection: Connection
  private val objectMapper = new ObjectMapper()

  @SuppressWarnings(Array("unused"))
  def deleteAllDataFromTable(tableName: String): Unit = {
    log.info(s"Deleting all records from table: $tableName")

    try {
      tryWithResources(connection.createStatement()) { stmt =>
        stmt.executeUpdate(s"DELETE FROM $tableName")
      }

      log.info(s"Table: $tableName was successfully wiped off")
    } catch {
      case e: Throwable => log.error(s"Cannot delete records from table: $tableName", e)
    }
  }

  def updateEmployeeDataVersion(versionType: String, newVersion: LocalDateTime): Boolean = {
    // TODO consider moving "Configuration" behavior to a common module
    var result: Boolean = false
    val configsBody: String = "{\"date\": \"" + TimeUtils.localDateTimeToString(newVersion) + "\"}"
    val sql = s"UPDATE configurations SET [configs] = '$configsBody' where [type]='$versionType'"

    log.info(s"Executing statement: $sql")
    try {
      result = tryWithResources(connection.createStatement()) { stmt =>
        var rowCount = stmt.executeUpdate(sql)
        if (rowCount == 0) {
          log.info("The update statement did not impact any rows. Retrying using an insert statement")
          val insert = s"INSERT INTO configurations ([type], [configs]) VALUES ('$versionType', '$configsBody')"
          rowCount = stmt.executeUpdate(insert)
        }
        val success = rowCount > 0
        if (success) {
          log.info(s"$versionType data version was successfully updated to: $newVersion")
        } else {
          log.info(s"$versionType data version could not be updated to: $newVersion")
        }
        success
      }
    } catch {
      case e: Throwable => log.error(s"Cannot set new $versionType data version as $newVersion", e)
    }
    result
  }

  // TODO Break large delete operations into chunks
  def deleteOldVersions(tableName: String, olderThan: LocalDateTime): Unit = {
    log.info(s"Deleting records older than $olderThan from table: $tableName")

    try {
      tryWithResources(connection.createStatement()) { stmt =>
        stmt.executeUpdate(s"DELETE FROM $tableName WHERE version < '" + TimeUtils.localDateTimeToString(olderThan) + "'")
      }
      log.info(s"Records older than $olderThan from Table: $tableName were successfully wiped off")
    } catch {
      case e: Throwable => log.error(s"Cannot delete records older than $olderThan from table: $tableName", e)
    }
  }

  private def tryWithResources[T <: AutoCloseable, V](r: => T)(f: T => V): V = {
    val resource: T = r
    require(resource != null, "resource is null")
    var exception: Throwable = null
    try {
      f(resource)
    } catch {
      case NonFatal(e) =>
        exception = e
        throw e
    } finally {
      closeAndAddSuppressed(exception, resource)
    }
  }

  private def closeAndAddSuppressed(e: Throwable, resource: AutoCloseable): Unit = {
    if (e != null) {
      try {
        resource.close()
      } catch {
        case NonFatal(suppressed) =>
          e.addSuppressed(suppressed)
      }
    } else {
      resource.close()
    }
  }

  def getIndexNameForConfigurationType(configurationType: String): Option[String] = {
    log.info(s"Getting index name for $configurationType")

    val indexNameFieldOpt =
      if (configurationType.equals(ConfigurationTypes.AzureSearchEmployeesIndex))
        Option(classOf[AzureSearchEmployeesIndexConfiguration].getDeclaredFields()(0).getName)
      else {
        log.error("Invalid configurationType argument! It must be AzureSearchEmployeesIndex.")
        None
      }

    if (indexNameFieldOpt.isDefined) {
      try {
        val stmt = connection.createStatement()
        val configurationsResultSet: ResultSet = stmt.executeQuery(f"""SELECT * FROM dbo.configurations where type = '$configurationType'""")
        if (configurationsResultSet.next()) {
          val jsonConfigs = configurationsResultSet.getString("configs")
          val configMap = objectMapper.readValue(jsonConfigs, classOf[java.util.Map[String, String]])
          val indexName = configMap.get(indexNameFieldOpt.get)
          log.info(s"Found configuration for $configurationType, containing index name $indexName")
          Option(indexName)
        } else None
      } catch {
        case e: Throwable => {
          log.error(s"Cannot find index name for $configurationType", e)
          None
        }
      }

    } else {
      None
    }
  }

}

object JdbcClient {

  def apply(username: String, password: String, jdbcUrl: String)
           (logger: GdcLogger): JdbcClient = {

    val ds = new SQLServerDataSource();
    ds.setURL(jdbcUrl)
    ds.setUser(username)
    ds.setPassword(password)

    new JdbcClient() {
      override val connection: Connection = ds.getConnection()
      override val log: GdcLogger = logger
    }
  }

  def apply(accessToken: String, jdbcUrl: String)
           (logger: GdcLogger): JdbcClient = {

    val ds = new SQLServerDataSource();
    ds.setURL(jdbcUrl)
    ds.setAccessToken(accessToken)

    new JdbcClient() {
      override val connection: Connection = ds.getConnection()
      override val log: GdcLogger = logger
    }
  }

  def apply(connectionProperties: Properties, jdbcUrl: String)(logger: GdcLogger): JdbcClient = {
    if (connectionProperties.containsKey("accessToken")) {
      JdbcClient(connectionProperties.getProperty("accessToken"), jdbcUrl)(logger)
    } else {
      JdbcClient(connectionProperties.getProperty("user"), connectionProperties.getProperty("password"), jdbcUrl)(logger)
    }
  }

}
