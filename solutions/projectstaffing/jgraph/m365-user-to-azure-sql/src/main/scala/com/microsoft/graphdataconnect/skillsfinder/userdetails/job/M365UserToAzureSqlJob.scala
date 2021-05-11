/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.userdetails.job

import java.time.{LocalDateTime, ZoneOffset}
import java.util.Properties

import com.microsoft.graphdataconnect.jdbc.JdbcClient
import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.model.configs.ConfigurationTypes
import com.microsoft.graphdataconnect.model.userdetails.db._
import com.microsoft.graphdataconnect.model.userdetails.source.gdc.UserDetails
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.config.GDCConfiguration
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.helpers.{BlobFileHelper, Constants}
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.managers.PicturesManager
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.models.UserManager
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.service.{GdcILogger, GraphService}
import com.microsoft.graphdataconnect.utils.TimeUtils
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}


class M365UserToAzureSqlJob()
                           (implicit configuration: GDCConfiguration,
                            implicit val sparkSession: SparkSession,
                            implicit val fileSystem: FileSystem)
  extends BlobFileHelper {

  var log: GdcLogger = _

  def start(jdbcHostname: String,
            jdbcPort: String,
            jdbcDatabase: String,
            maxDbConnections: Int = 8,
            graphApiAccessToken: String,
            ingestionMode: String): Unit = {

    log = configuration.getLogger(classOf[M365UserToAzureSqlJob])
    // TODO retrieve all configs from GDCConfiguration
    // Configs
    val usersDataInputPath = configuration.getM365UserDataFullPath
    val managerDataInputPath = configuration.getM365UserManagerFullPath

    // Create the JDBC URL without passing in the user and password parameters.
    val jdbcUrl = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

    val connectionProperties: Properties = configuration.getDatabaseConnectionProperties()
    val jdbcClient = JdbcClient(connectionProperties, jdbcUrl)(log)

    import sparkSession.implicits._

    log.info("Reading M365 managers data from input directory")
    val m365UsersManagersDf = sparkSession.read.json(managerDataInputPath)
    val m365UsersManagersDs = m365UsersManagersDf.select("puser", "displayName", "mail").as[UserManager]

    log.info("Reading M365 users data from input directory")
    var m365UsersDf: DataFrame = sparkSession.read.json(usersDataInputPath)

    if (!m365UsersDf.columns.contains("picture")) {
      m365UsersDf = m365UsersDf.withColumn("picture", lit(""))
    }

    m365UsersDf = m365UsersDf.select("id", "mail", "displayName", "aboutMe", "jobTitle",
      "companyName", "department", "officeLocation", "city", "state", "country",
      "skills", "responsibilities", "pastProjects", "schools", "interests", "picture")

    val m365UsersDs = m365UsersDf.as[UserDetails]

    val usersWithManagersDs = m365UsersDs.joinWith(m365UsersManagersDs, m365UsersDf("id") === m365UsersManagersDs("puser"), "left")

    // defining the next version for Employee data
    val employeeDataVersion = LocalDateTime.now(ZoneOffset.UTC)
    val employeeDataVersionStr = TimeUtils.localDateTimeToString(employeeDataVersion)

    val loggerWorkspaceId = configuration.getLoggerWorkspaceId()
    val logType = configuration.getLogType()
    val sharedKey = configuration.getLoggerWorkspaceKey()

    val employeeProfilesDs = usersWithManagersDs.mapPartitions { partition => {

      val logger = new GdcILogger(GdcLoggerFactory.getLogger(logAnalyticsWorkspaceId = loggerWorkspaceId,
        logAnalyticsSharedKey = sharedKey,
        clazz = classOf[M365UserToAzureSqlJob],
        logType = logType))
      val graphService = GraphService(graphApiAccessToken, logger)
      val picturesManager: PicturesManager = managers.PicturesManager(graphService, logger, ingestionMode)

      partition.map {
        case (userDetails: UserDetails, userManager: UserManager) =>
          val profilePicture = picturesManager.getProfilePicture(userDetails)
          EmployeeProfile(
            userDetails,
            profilePicture,
            reportsTo = userManager.displayName,
            managerEmail = userManager.mail,
            version = employeeDataVersionStr)
        case (userDetails: UserDetails, _) =>
          val profilePicture = picturesManager.getProfilePicture(userDetails)
          EmployeeProfile(
            userDetails,
            profilePicture,
            reportsTo = null,
            managerEmail = null,
            version = employeeDataVersionStr)
      }
    }
    }

    def isNullOrBlank = udf((input: String) => {
      input == null || input.trim.isEmpty
    })

    sparkSession.udf.register("isNullOrBlank", isNullOrBlank)

    def trimStrings = udf((attributes: Seq[String]) => {
      attributes.map(_.trim())
    })

    sparkSession.udf.register("trimStrings", trimStrings)

    val employeeInterestsDs = m365UsersDs
      .withColumn("interests", expr("filter(interests, attribute -> !isNullOrBlank(attribute))").as("interests"))
      .withColumn("interests", trimStrings($"interests").as("interests"))
      .select(
        col("id").as("employee_profile_id"),
        lit(employeeDataVersionStr).as("employee_profile_version"),
        explode(col("interests")).as("interest")
      ).as[Interest]
    val employeePastProjectsDs = m365UsersDs
      .withColumn("pastProjects", expr("filter(pastProjects, attribute -> !isNullOrBlank(attribute))").as("pastProjects"))
      .withColumn("pastProjects", trimStrings($"pastProjects").as("pastProjects"))
      .select(
        col("id").as("employee_profile_id"),
        lit(employeeDataVersionStr).as("employee_profile_version"),
        explode(col("pastProjects")).as("past_project")
      ).as[PastProject]
    val employeeResponsibilitiesDs = m365UsersDs
      .withColumn("responsibilities", expr("filter(responsibilities, attribute -> !isNullOrBlank(attribute))").as("responsibilities"))
      .withColumn("responsibilities", trimStrings($"responsibilities").as("responsibilities"))
      .select(
        col("id").as("employee_profile_id"),
        lit(employeeDataVersionStr).as("employee_profile_version"),
        explode(col("responsibilities")).as("responsibility")
      ).as[Responsibility]
    val employeeSchoolsDs = m365UsersDs
      .withColumn("schools", expr("filter(schools, attribute -> !isNullOrBlank(attribute))").as("schools"))
      .withColumn("schools", trimStrings($"schools").as("schools"))
      .select(
        col("id").as("employee_profile_id"),
        lit(employeeDataVersionStr).as("employee_profile_version"),
        explode(col("schools")).as("school")
      ).as[School]
    val employeeSkillsDs = m365UsersDs
      .withColumn("skills", expr("filter(skills, attribute -> !isNullOrBlank(attribute))").as("skills"))
      .withColumn("skills", trimStrings($"skills").as("skills"))
      .select(
        col("id").as("employee_profile_id"),
        lit(employeeDataVersionStr).as("employee_profile_version"),
        explode(col("skills")).as("skill")
      ).as[Skill]

    // Write the new data into the table using a new version (employeeDataVersion)
    employeeProfilesDs.coalesce(maxDbConnections) // Trying to keep the number of connections to the database small
      .write.mode(SaveMode.Append)
      .jdbc(jdbcUrl, Constants.EMPLOYEE_PROFILE_TABLE_NAME, connectionProperties)

    employeeInterestsDs.coalesce(maxDbConnections).write.mode(SaveMode.Append)
      .jdbc(jdbcUrl, Constants.EMPLOYEE_INTERESTS_TABLE_NAME, connectionProperties)

    employeePastProjectsDs.coalesce(maxDbConnections).write.mode(SaveMode.Append)
      .jdbc(jdbcUrl, Constants.EMPLOYEE_PAST_PROJECTS_TABLE_NAME, connectionProperties)

    employeeResponsibilitiesDs.coalesce(maxDbConnections).write.mode(SaveMode.Append)
      .jdbc(jdbcUrl, Constants.EMPLOYEE_RESPONSIBILITIES_TABLE_NAME, connectionProperties)

    employeeSchoolsDs.coalesce(maxDbConnections).write.mode(SaveMode.Append)
      .jdbc(jdbcUrl, Constants.EMPLOYEE_SCHOOLS_TABLE_NAME, connectionProperties)

    employeeSkillsDs.coalesce(maxDbConnections).write.mode(SaveMode.Append)
      .jdbc(jdbcUrl, Constants.EMPLOYEE_SKILLS_TABLE_NAME, connectionProperties)

    // update the version for Employee data
    val versionUpdatedSuccessfully = jdbcClient.updateEmployeeDataVersion(ConfigurationTypes.LatestVersionOfEmployeeProfile, newVersion = employeeDataVersion)
    if (versionUpdatedSuccessfully) {
      log.info("Successfully stored users profile data")
      log.info("Waiting for Core to refresh the data...")
      Thread.sleep(60000)
      log.info("Deleting old records...")
      jdbcClient.deleteOldVersions(Constants.EMPLOYEE_PROFILE_TABLE_NAME, olderThan = employeeDataVersion)
      log.info("Done")
    } else {
      // throw exception and let ADF to handle it
      throw new Exception(s"Could not set new version [$employeeDataVersionStr] on Employee profile data")
    }
  }

}
