/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata

import javax.persistence.{EmbeddedId, _}


@Table(name = "hr_data_employee_profile")
@Entity
class HRDataEmployeeProfile {

  @EmbeddedId
  var composedId: HRDataEmployeeIdentity = _

  @Column(name = "name")
  var name: String = _

  @Column(name = "available_starting_from")
  var availableStartingFrom: String = _

  @Column(name = "role")
  var role: String = _

  @Column(name = "employee_type")
  var employeeType: String = _

  @Column(name = "current_engagement")
  var currentEngagement: String = _

  @Column(name = "department")
  var department: String = _

  @Column(name = "company_name")
  var companyName: String = _

  @Column(name = "manager_name")
  var managerName: String = _

  @Column(name = "manager_email")
  var managerEmail: String = _

  @Column(name = "country")
  var country: String = _

  @Column(name = "state")
  var state: String = _

  @Column(name = "city")
  var city: String = _

  @Column(name = "location")
  var location: String = _

  @Column(name = "office_location")
  var officeLocation: String = _

  @Column(name = "linkedin_profile")
  var linkedInProfile: String = _

}
