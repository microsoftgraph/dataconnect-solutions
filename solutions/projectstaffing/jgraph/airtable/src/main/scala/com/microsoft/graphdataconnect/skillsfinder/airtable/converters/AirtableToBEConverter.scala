/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.converters

import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.{AirtableConsultancyType, AirtableCurrentEngagement, AirtableEmployee, AirtableRole}
import com.microsoft.graphdataconnect.skillsfinder.airtable.model.be
import com.microsoft.graphdataconnect.skillsfinder.airtable.model.be.AirtableEmployeeDetails

object AirtableToBEConverter {

  def convertEmployee(airtableEmployee: AirtableEmployee,
                      airtableEmployees: Map[String, AirtableEmployee],
                      airtableCurrentEngagements: Map[String, AirtableCurrentEngagement],
                      airtableRoles: Map[String, AirtableRole],
                      airtableConsultancyTypes: Map[String, AirtableConsultancyType]): AirtableEmployeeDetails = {


    val currentEngagement: Option[AirtableCurrentEngagement] = Option(airtableEmployee.currentEngagement).map(_.head)
      .flatMap(airtableCurrentEngagements.get)

    val typeOfConsultant: Option[AirtableConsultancyType] = Option(airtableEmployee.typeOfConsultant).map(_.head)
      .flatMap(airtableConsultancyTypes.get)

    val role: Option[AirtableRole] = Option(airtableEmployee.role).map(_.head)
      .flatMap(airtableRoles.get)

    val reportsTo: Option[AirtableEmployee] = Option(airtableEmployee.reportsTo).map(_.head)
      .flatMap(airtableEmployees.get)

    be.AirtableEmployeeDetails(
      mail = airtableEmployee.emailAddress,
      name = airtableEmployee.name,
      locate = airtableEmployee.located,
      up_for_redeployment_date = airtableEmployee.upForRedeploymentDate,
      current_engagement = currentEngagement.map(_.name).orNull,
      reports_to = reportsTo.map(_.name).orNull,
      manager_email = reportsTo.map(_.emailAddress).orNull,
      role = role.map(_.name).orNull,
      consultant_type = typeOfConsultant.map(_.name).orNull,
      linkedin_profile = airtableEmployee.linkedInProfile
    )
  }

}

