package com.microsoft.graphdataconnect.skillsfinder.db.entities.employee

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence._


@Table(name = "employee_profile")
@Entity
@SqlResultSetMapping(
  name = "recommendedEmployeesMapping",
  classes = Array(
    new ConstructorResult(
      targetClass = classOf[RecommendedEmployee],
      columns = Array(
        new ColumnResult(name = "employee_id", `type` = classOf[String]),
        new ColumnResult(name = "name", `type` = classOf[String]),
        new ColumnResult(name = "mail", `type` = classOf[String]),
        new ColumnResult(name = "employee_role", `type` = classOf[String]),
        new ColumnResult(name = "inferred_roles_json", `type` = classOf[String]),
        new ColumnResult(name = "location", `type` = classOf[String]),
        new ColumnResult(name = "about_me", `type` = classOf[String]),
        new ColumnResult(name = "current_engagement", `type` = classOf[String]),
        new ColumnResult(name = "department", `type` = classOf[String]),
        new ColumnResult(name = "reports_to", `type` = classOf[String]),
        new ColumnResult(name = "manager_email", `type` = classOf[String]),
        new ColumnResult(name = "available_since", `type` = classOf[String]),
        new ColumnResult(name = "linkedin_profile", `type` = classOf[String]),
        new ColumnResult(name = "included_in_current_team", `type` = classOf[Boolean]),
        new ColumnResult(name = "total", `type` = classOf[Int]),
        new ColumnResult(name = "skills_json", `type` = classOf[String]),
        new ColumnResult(name = "profile_picture", `type` = classOf[String])
      )
    )
  )
)
@NamedNativeQueries(
  Array(
    new NamedNativeQuery(
      name = "EmployeeProfile.findRecommendedEmployees",
      query = "EXECUTE find_recommended_employees :size_param, :offset_param, :order_by_availability_and_name, :available_since, :team_owner_email, :recommended_candidates_emails, :latest_profile_version, :latest_hr_data_version, :latest_inferred_roles_version, :is_hr_data_mandatory, :json_filters",
      resultSetMapping = "recommendedEmployeesMapping"
    )
  )
)
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

  @JsonProperty("profile_picture")
  @Column(name = "profile_picture")
  var profilePicture: String = _

  @JsonProperty("reports_to")
  @Column(name = "reports_to")
  var reportsTo: String = _

  @JsonProperty("manager_email")
  @Column(name = "manager_email")
  var managerEmail: String = _

  @OneToMany(mappedBy = "employeeProfile")
  var skills: java.util.List[Skill] = _

  @OneToMany(mappedBy = "employeeProfile")
  var interests: java.util.List[Interest] = _

  @OneToMany(mappedBy = "employeeProfile")
  var pastProjects: java.util.List[PastProject] = _

  @OneToMany(mappedBy = "employeeProfile")
  var schools: java.util.List[School] = _

  @OneToMany(mappedBy = "employeeProfile")
  var responsibilities: java.util.List[Responsibility] = _

}
