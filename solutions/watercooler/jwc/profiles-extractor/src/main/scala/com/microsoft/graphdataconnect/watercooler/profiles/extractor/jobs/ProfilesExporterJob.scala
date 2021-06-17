/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */


package com.microsoft.graphdataconnect.watercooler.profiles.extractor.jobs

import java.time.LocalDateTime
import java.util.{Base64, Properties}

import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.EmployeeProfileCC
import com.microsoft.graphdataconnect.watercooler.common.dto.UserDetailsDTO
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import com.microsoft.graphdataconnect.watercooler.common.services.graph.GraphService
import com.microsoft.graphdataconnect.watercooler.common.util.{Constants, TimeUtils}
import com.microsoft.graphdataconnect.watercooler.profiles.extractor.models.UserDTO
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}

class ProfilesExporterJob extends Serializable {

  def run(sparkSession: SparkSession, usersDTO: List[UserDTO], newEmployeeProfileVersion: LocalDateTime)
         (jdbcUrl: String, graphApiAppAccessInfo: GraphApiAppAccessInfo, maxDbConnections: Int, connectionProperties: Properties): Unit = {

    import sparkSession.implicits._

    val usersRdd: RDD[UserDTO] = sparkSession.sparkContext.parallelize(usersDTO)
    val usersDS: Dataset[UserDTO] = sparkSession.createDataset(usersRdd)

    val employeeProfileCC: Dataset[EmployeeProfileCC] = usersDS.mapPartitions(partition => {
      val graphService: GraphService = GraphService(graphApiAppAccessInfo)

      partition.map { user =>
        val userInfo: UserDetailsDTO = graphService.getUserInfo(user.id)
        val profilePicture: String = encodePhoto(graphService.readUserProfilePicture(user.mail))

        EmployeeProfileCC(
          id = user.id,
          version = TimeUtils.localDateTimeToString(newEmployeeProfileVersion),
          mail = user.mail,
          display_name = userInfo.displayName,
          about_me = userInfo.aboutMe,
          job_title = userInfo.jobTitle,
          company_name = userInfo.companyName,
          department = userInfo.department,
          country = userInfo.country,
          office_location = userInfo.officeLocation,
          city = userInfo.city,
          state = userInfo.state,
          skills = userInfo.skills,
          responsibilities = userInfo.responsibilities,
          engagement = userInfo.engagement,
          image = profilePicture
        )
      }
    })

    employeeProfileCC.coalesce(maxDbConnections) // Trying to keep the number of connections to the database small
      .write.mode(SaveMode.Append)
      .jdbc(jdbcUrl, Constants.EMPLOYEE_PROFILES_JDBC_TABLE_NAME, connectionProperties)
  }

  private def encodePhoto(photoBytes: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(photoBytes)
  }

}

object ProfilesExporterJob {
  def apply(): ProfilesExporterJob = new ProfilesExporterJob()
}

