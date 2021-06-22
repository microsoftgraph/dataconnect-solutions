/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */


package com.microsoft.graphdataconnect.watercooler.profiles.extractor.jobs

import java.time.LocalDateTime
import java.util.{Base64, Properties}

import com.microsoft.graph.models.User
import com.microsoft.graphdataconnect.watercooler.common.db.JdbcClient
import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLogger
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import com.microsoft.graphdataconnect.watercooler.common.services.graph.GraphService
import com.microsoft.graphdataconnect.watercooler.common.util.Constants
import com.microsoft.graphdataconnect.watercooler.profiles.extractor.config.JwcConfiguration
import com.microsoft.graphdataconnect.watercooler.profiles.extractor.models.UserDTO
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.codehaus.jackson.map.ObjectMapper


class ProfilesExporterWrapper(jdbcClient: JdbcClient, jdbcUrl: String, connectionProperties: Properties, maxDbConnections: Int)
                             (implicit val jwcConfiguration: JwcConfiguration,
                       implicit val sparkContext: SparkContext,
                       implicit val sparkSession: SparkSession,
                       implicit val log: JwcLogger,
                       implicit val newEmployeeProfileVersion: LocalDateTime,
                       implicit val objectMapper: ObjectMapper) extends Serializable {


  def start(graphApiAppAccessInfo: GraphApiAppAccessInfo): Unit = {

    val graphService: GraphService = GraphService(graphApiAppAccessInfo)

    val users: List[User] = graphService.getUsers
    // filter users
    val filteredUsers = users.filter(x => x.mail != null)

    val usersDTO: List[UserDTO] = filteredUsers.map(user => UserDTO(user.id, user.mail))

    ProfilesExporterJob().run(sparkSession, usersDTO, newEmployeeProfileVersion)(jdbcUrl, graphApiAppAccessInfo, maxDbConnections, connectionProperties)

    // Update Employee Profile data version
    val versionUpdatedSuccessfully = jdbcClient.upsertEmployeeDataVersion(newEmployeeProfileVersion)
    if (versionUpdatedSuccessfully) {
      log.info("Successfully stored employee profile data")
      log.info("Waiting for Core to refresh the data...")
      Thread.sleep(60000)
      log.info("Deleting old records...")
      jdbcClient.deleteOldVersions(Constants.EMPLOYEE_PROFILES_JDBC_TABLE_NAME, olderThan = newEmployeeProfileVersion)
      log.info("Done")
    } else {
      // throw exception and let ADF to handle it
      throw new Exception(s"Could not set [$newEmployeeProfileVersion] as Employee Profile latest data version ")
    }

  }

  private def createUserInsert(d: Tuple14[String, String, String, String, String, String, String, String, String, String, String, String, String, String]): String = {
    "INSERT INTO dbo.employee_profile (id,mail,display_name,about_me,job_title,company_name,department,country,office_location,city,state,skills,responsibilities,engagement) VALUES " +
      s"('${d._1}', '${d._2}', '${d._3}', '${d._4}', '${d._5}', '${d._6}', '${d._7}', '${d._8}', '${d._9}', '${d._10}', '${d._11}', '${d._12}', '${d._13}', '${d._14}');"
  }

  private def createPictureUpdate(id: String, email: String, graphService: GraphService): String = {
    val profilePicture: String = encodePhoto(graphService.readUserProfilePicture(email))
    s"UPDATE dbo.employee_profile SET image = '" + profilePicture + "' WHERE id='" + id + "';"
  }

  private def encodePhoto(photoBytes: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(photoBytes)
  }

}

object ProfilesExporterWrapper {

  def apply(jdbcClient: JdbcClient, jdbcUrl: String, connectionProperties: Properties, maxDbConnections: Int)
           (implicit jwcConfiguration: JwcConfiguration,
            sparkContext: SparkContext,
            sparkSession: SparkSession,
            log: JwcLogger,
            newEmployeeProfileVersion: LocalDateTime,
            objectMapper: ObjectMapper): ProfilesExporterWrapper =

    new ProfilesExporterWrapper(jdbcClient, jdbcUrl, connectionProperties, maxDbConnections)(jwcConfiguration, sparkContext, sparkSession, log, newEmployeeProfileVersion, objectMapper)

}


