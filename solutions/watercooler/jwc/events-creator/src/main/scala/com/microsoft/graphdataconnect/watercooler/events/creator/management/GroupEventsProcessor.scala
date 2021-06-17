/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator.management

import java.time.LocalDate
import java.util.Properties

import com.microsoft.graphdataconnect.watercooler.events.creator.config.SendMailConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.watercooler.common.db.JdbcClient
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerDayCC
import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLogger
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.events.creator.config.{AppConfig, SendMailConfig}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

class GroupEventsProcessor(val jdbcUrl: String,
                           val dbProperties: Properties)
                          (implicit val log: JwcLogger,
                           val graphApiAppAccessInfo: GraphApiAppAccessInfo,
                           val sendMailConfig: SendMailConfig,
                           val sparkSession: SparkSession) extends Serializable {

  def start(startDate: LocalDate,
            endDate: LocalDate,
            meetingOrganizerEmail: String): Unit = {

    log.info("Starting to process groups...")
    import sparkSession.implicits._

    val driverJdbcClient: JdbcClient = JdbcClient(jdbcUrl, dbProperties)(log);
    val latestGroupsVersion = driverJdbcClient.getLatestVersionOfGroups()

    val groupsPerDayJdbcDF: DataFrame = sparkSession.read.jdbc(jdbcUrl, "groups_per_day", dbProperties)
    val groupsPerDayDF: Dataset[GroupPerDayCC] = groupsPerDayJdbcDF.as[GroupPerDayCC]

    val filteredGroups: Dataset[GroupPerDayCC] = groupsPerDayDF.filter { group =>
      TimeUtils.databaseTimestampToLocalDateTime(group.version).isEqual(TimeUtils.timestampToLocalDateTime(latestGroupsVersion)) &&
      TimeUtils.isAfterOrEqual(TimeUtils.databaseTimestampToLocalDateTime(group.hour_time_slot).toLocalDate, startDate) &&
      TimeUtils.isBeforeOrEqual(TimeUtils.databaseTimestampToLocalDateTime(group.hour_time_slot).toLocalDate, endDate)
    }

    val processed: Dataset[Boolean] = filteredGroups.mapPartitions { partition =>
      implicit val objectMapper: ObjectMapper = AppConfig.objectMapperBuilder.build[ObjectMapper]()

      val groupMailBodyWriter: GroupMailBodyWriter = new GroupMailBodyWriter()
      val teamsMeetingCreator: TeamsMeetingCreator = new TeamsMeetingCreator()
      val calendarEventCreator: CalendarEventCreator = new CalendarEventCreator(groupMailBodyWriter, teamsMeetingCreator, sendMailConfig)

      partition.map { group =>
        calendarEventCreator.sendInvite(graphApiAppAccessInfo, group, meetingOrganizerEmail)
      }
    }

    val totalEventsCreated = processed.count()
    log.info(s"Total number of created events: $totalEventsCreated")

  }
}

object GroupEventsProcessor {

  def apply(jdbcUrl: String,
            connectionProperties: Properties)
          (implicit log: JwcLogger,
          graphApiAppAccessInfo: GraphApiAppAccessInfo,
          sendMailConfig: SendMailConfig,
          sparkSession: SparkSession): GroupEventsProcessor =

    new GroupEventsProcessor(jdbcUrl, connectionProperties)(log, graphApiAppAccessInfo, sendMailConfig, sparkSession)

}
