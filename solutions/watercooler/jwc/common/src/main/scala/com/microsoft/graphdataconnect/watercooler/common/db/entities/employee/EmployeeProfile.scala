/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.entities.employee

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence._


@Table(name = "employee_profile")
@Entity
class EmployeeProfile {

  @EmbeddedId
  var composedId: EmployeeIdentity = _

  @JsonProperty("mail")
  @Column(name = "mail")
  var mail: String = _

  @JsonProperty("displayName")
  @Column(name = "display_name")
  var displayName: String = _

  @JsonProperty("aboutMe")
  @Column(name = "about_me", columnDefinition = "text")
  var aboutMe: String = _

  @JsonProperty("jobTitle")
  @Column(name = "job_title")
  var jobTitle: String = _

  @JsonProperty("companyName")
  @Column(name = "company_name")
  var companyName: String = _

  @JsonProperty("department")
  @Column(name = "department")
  var department: String = _

  @JsonProperty("officeLocation")
  @Column(name = "office_location")
  var officeLocation: String = _

  @JsonProperty("city")
  @Column(name = "city")
  var city: String = _

  @JsonProperty("state")
  @Column(name = "state")
  var state: String = _

  @JsonProperty("country")
  @Column(name = "country")
  var country: String = _

  @JsonProperty("skills")
  @Column(name = "skills")
  var skills: String = _

  @JsonProperty("responsibilities")
  @Column(name = "responsibilities")
  var responsibilities: String = _

  @JsonProperty("engagement")
  @Column(name = "engagement")
  var engagement: String = _

  @JsonProperty("image")
  @Column(name = "image", columnDefinition = "text")
  var image: String = _

}
