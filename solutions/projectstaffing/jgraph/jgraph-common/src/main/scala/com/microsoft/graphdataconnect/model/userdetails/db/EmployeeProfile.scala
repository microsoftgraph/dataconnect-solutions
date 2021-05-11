/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.userdetails.db

import com.microsoft.graphdataconnect.model.userdetails.source.gdc.UserDetails

// Class meant to be used to convert a Spark DataSet into a database table.
// Must match the exact schema in the database. This is the reason we do not follow the scala field naming convention
case class EmployeeProfile(
                            id: String,
                            version: String,
                            mail: String,
                            display_name: String,
                            about_me: String,
                            job_title: String,
                            company_name: String,
                            department: String,
                            office_location: String,
                            city: String,
                            state: String,
                            country: String,
                            profile_picture: String,
                            reports_to: String,
                            manager_email: String
                          )

// TODO consider doing this conversion directly in the spark job code
object EmployeeProfile {
  def apply(userDetails: UserDetails,
            profilePicture: String,
            reportsTo: String,
            managerEmail: String,
            version: String): EmployeeProfile = {
    new EmployeeProfile(
      userDetails.id,
      version,
      if (userDetails.mail != null) userDetails.mail.trim else userDetails.mail,
      userDetails.displayName,
      userDetails.aboutMe,
      userDetails.jobTitle,
      userDetails.companyName,
      userDetails.department,
      userDetails.officeLocation,
      userDetails.city,
      userDetails.state,
      userDetails.country,
      profilePicture,
      reportsTo,
      if (managerEmail != null) managerEmail.trim else managerEmail
    )
  }
}