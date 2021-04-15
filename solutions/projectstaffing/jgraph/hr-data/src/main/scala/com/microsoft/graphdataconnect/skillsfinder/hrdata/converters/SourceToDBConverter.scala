package com.microsoft.graphdataconnect.skillsfinder.hrdata.converters

import com.microsoft.graphdataconnect.model.userdetails.db.HRDataEmployeeProfile
import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails

object SourceToDBConverter {

  def convertEmployee(employeeDetails: HRDataEmployeeDetails, timeVersion: String): HRDataEmployeeProfile = {
    HRDataEmployeeProfile(
      version = timeVersion,
      mail = employeeDetails.mail.trim,
      name = employeeDetails.name,
      available_starting_from = employeeDetails.availableStartingFrom,
      role = employeeDetails.role,
      employee_type = employeeDetails.employeeType,
      current_engagement = employeeDetails.currentEngagement,
      department = employeeDetails.department,
      company_name = employeeDetails.companyName,
      manager_name = employeeDetails.managerName,
      manager_email = employeeDetails.managerEmail,
      country = employeeDetails.country,
      state = employeeDetails.state,
      city = employeeDetails.city,
      location = employeeDetails.location,
      office_location = employeeDetails.officeLocation,
      linkedin_profile = employeeDetails.linkedInProfile)
  }
}
