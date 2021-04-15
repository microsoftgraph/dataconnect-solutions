package com.microsoft.graphdataconnect.model.userdetails.db

// Corresponds to the hr_data_employee_profile table in the DB
case class HRDataEmployeeProfile(mail: String,
                                 version: String,
                                 name: String,
                                 available_starting_from: String,
                                 role: String,
                                 employee_type: String,
                                 current_engagement: String,
                                 department: String,
                                 company_name: String,
                                 manager_name: String,
                                 manager_email: String,
                                 country: String,
                                 state: String,
                                 city: String,
                                 location: String,
                                 office_location: String,
                                 linkedin_profile: String)
