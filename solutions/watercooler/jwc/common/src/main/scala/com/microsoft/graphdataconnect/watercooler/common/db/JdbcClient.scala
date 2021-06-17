/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db

import java.sql.Connection
import java.time.LocalDateTime
import java.util.Properties

import com.microsoft.graphdataconnect.watercooler.common.db.entities.meta.ConfigurationTypes
import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLogger
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.sqlserver.jdbc.SQLServerDataSource

import scala.util.control.NonFatal

trait JdbcClient {

  val log: JwcLogger

  // TODO add a connection pool manager [eg: c3p0]
  val connection: Connection

  def upsertEmployeeDataVersion(newVersion: LocalDateTime): Boolean = {
    if (isEmployeeDataVersionSet()) updateEmployeeDataVersion(newVersion) else createEmployeeDataVersion(newVersion)
  }

  def getLatestVersionOfGroups(): String = {
    var result: String = TimeUtils.oldestSqlDateStr
    try {
      result = tryWithResources(connection.createStatement()) { stmt =>
        val rs = stmt.executeQuery(s"SELECT [value] as vl FROM configurations WHERE [key_name] = '${ConfigurationTypes.LatestVersionOfGroups}'")
        if (rs.next()) rs.getString("vl") else TimeUtils.oldestSqlDateStr
      }
    } catch {
      case e: Throwable => log.error(s"Cannot find if Employee data version is set", e)
    }
    result
  }

  private def updateEmployeeDataVersion(newVersion: LocalDateTime): Boolean = {
    var result: Boolean = false
    val dateStr: String = TimeUtils.localDateTimeToString(newVersion)

    try {
      result = tryWithResources(connection.createStatement()) { stmt =>
        stmt.executeUpdate(s"UPDATE configurations set [value] = '$dateStr' where [key_name]='${ConfigurationTypes.LatestVersionOfEmployeeProfiles}'") > 0
      }
      log.info(s"Employee data version was successfully updated to: $newVersion")
    } catch {
      case e: Throwable => log.error(s"Cannot update new Employee data version as $newVersion", e)
    }
    result
  }

  private def createEmployeeDataVersion(newVersion: LocalDateTime): Boolean = {
    var result: Boolean = false
    val dateStr: String = TimeUtils.localDateTimeToString(newVersion)
    try {
      result = tryWithResources(connection.createStatement()) { stmt =>
        stmt.executeUpdate(s"INSERT INTO configurations([key_name], [value]) VALUES ('${ConfigurationTypes.LatestVersionOfEmployeeProfiles}', '$dateStr')") > 0
      }
      log.info(s"Employee data version was successfully updated to: $newVersion")
    } catch {
      case e: Throwable => log.error(s"Cannot set new Employee data version as $newVersion", e)
    }
    result
  }



  private def isEmployeeDataVersionSet(): Boolean = {
    var result: Boolean = false
    try {
      result = tryWithResources(connection.createStatement()) { stmt =>
        stmt.executeQuery(s"SELECT [value] FROM configurations WHERE [key_name] = '${ConfigurationTypes.LatestVersionOfEmployeeProfiles}'").next()
      }
    } catch {
      case e: Throwable => log.error(s"Cannot find if Employee data version is set", e)
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

}

object JdbcClient {

  def apply(jdbcUrl: String, username: String, password: String)
           (logger: JwcLogger): JdbcClient = {

    val ds = new SQLServerDataSource();
    ds.setURL(jdbcUrl)
    ds.setUser(username)
    ds.setPassword(password)

    new JdbcClient() {
      override val connection: Connection = ds.getConnection()
      override val log: JwcLogger = logger
    }
  }

  def apply(jdbcUrl: String, accessToken: String)
           (logger: JwcLogger): JdbcClient = {

    val ds = new SQLServerDataSource();
    ds.setURL(jdbcUrl)
    ds.setAccessToken(accessToken)

    new JdbcClient() {
      override val connection: Connection = ds.getConnection()
      override val log: JwcLogger = logger
    }
  }

  def apply(jdbcUrl: String, connectionProperties: Properties)(logger: JwcLogger): JdbcClient = {
    if (connectionProperties.containsKey("accessToken")) {
      JdbcClient(jdbcUrl, connectionProperties.getProperty("accessToken"))(logger)
    } else {
      JdbcClient(jdbcUrl, connectionProperties.getProperty("user"), connectionProperties.getProperty("password"))(logger)
    }
  }

}
