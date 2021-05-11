/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.access

import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api.AirtableRecords
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.{AirtableConsultancyType, AirtableCurrentEngagement, AirtableEmployee, AirtableRole}
import com.microsoft.graphdataconnect.skillsfinder.airtable.config.{Constants, GDCConfiguration}
import kong.unirest.GenericType

class AirtableReader(baseKey: String, apiKey: String)
                    (implicit configuration: GDCConfiguration) {

  def readEmployees: Map[String, AirtableEmployee] = {
    val genericType = new GenericType[AirtableRecords[AirtableEmployee]] {}
    val employees: Seq[AirtableEmployee] = AirtableClient[AirtableEmployee](baseKey, apiKey, genericType)
      .getAirtableRecords(Constants.AIRTABLE_EMPLOYEES_TABLE_NAME)

    employees.map((employee: AirtableEmployee) => {
      employee.id -> employee
    }).toMap
  }

  def readRoles: Map[String, AirtableRole] = {
    val genericType = new GenericType[AirtableRecords[AirtableRole]] {}
    val roles: Seq[AirtableRole] = AirtableClient[AirtableRole](baseKey, apiKey, genericType)
      .getAirtableRecords(Constants.AIRTABLE_ROLES_TABLE_NAME, isMandatory = false)

    roles.map((role: AirtableRole) => {
      role.id -> role
    }).toMap
  }

  def readEngagements: Map[String, AirtableCurrentEngagement] = {
    val genericType = new GenericType[AirtableRecords[AirtableCurrentEngagement]] {}
    val engagements: Seq[AirtableCurrentEngagement] = AirtableClient[AirtableCurrentEngagement](baseKey, apiKey, genericType)
      .getAirtableRecords(Constants.AIRTABLE_OFFICIAL_ENGAGEMENTS_TABLE_NAME, isMandatory = false)

    engagements.map((engagement: AirtableCurrentEngagement) => {
      engagement.id -> engagement
    }).toMap
  }

  def readConsultancyType: Map[String, AirtableConsultancyType] = {
    val genericType = new GenericType[AirtableRecords[AirtableConsultancyType]] {}
    val consultancyTypes: Seq[AirtableConsultancyType] = AirtableClient[AirtableConsultancyType](baseKey, apiKey, genericType)
      .getAirtableRecords(Constants.AIRTABLE_TYPE_OF_CONSULTANT_TABLE_NAME, isMandatory = false)

    consultancyTypes.map((consultancyType: AirtableConsultancyType) => {
      consultancyType.id -> consultancyType
    }).toMap
  }

}

object AirtableReader {
  def apply(baseKey: String, apiKey: String)
           (implicit configuration: GDCConfiguration): AirtableReader = new AirtableReader(baseKey, apiKey)
}