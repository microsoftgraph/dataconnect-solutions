/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.converters

import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.airtable.model.be.AirtableEmployeeDetails

object AirtableBEToHRDataConverter {

  def convertEmployee(airtableEmployeeDetails: AirtableEmployeeDetails): HRDataEmployeeDetails = {
    HRDataEmployeeDetails(
      mail = airtableEmployeeDetails.mail.trim,
      name = airtableEmployeeDetails.name,
      availableStartingFrom = airtableEmployeeDetails.up_for_redeployment_date,
      role = airtableEmployeeDetails.role,
      employeeType = airtableEmployeeDetails.consultant_type,
      currentEngagement = airtableEmployeeDetails.current_engagement,
      department = null,
      companyName = null,
      managerName = airtableEmployeeDetails.reports_to,
      managerEmail = airtableEmployeeDetails.manager_email,
      country = null,
      state = null,
      city = null,
      location = airtableEmployeeDetails.locate,
      officeLocation = null,
      linkedInProfile = airtableEmployeeDetails.linkedin_profile)
  }
}
