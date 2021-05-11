/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models

import com.google.gson.annotations.SerializedName
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api.Identity

class AirtableEmployee extends Identity {

  @SerializedName("Status")
  var status: String = _

  @SerializedName("Email Address")
  var emailAddress: String = _

  @SerializedName("Employee Name")
  var name: String = _

  @SerializedName("Located")
  var located: String = _

  @SerializedName("Up for Redeployment Date")
  var upForRedeploymentDate: String = _

  @SerializedName("Current Engagement End Date")
  var currentEngagementEndDate: String = _

  @SerializedName("Current Engagement")
  var currentEngagement: Array[String] = _

  @SerializedName("Reports to")
  var reportsTo: Array[String] = _

  @SerializedName("Role")
  var role: Array[String] = _

  @SerializedName("Type of Consultant")
  var typeOfConsultant: Array[String] = _

  @SerializedName("LinkedIn")
  var linkedInProfile: String = _
}
