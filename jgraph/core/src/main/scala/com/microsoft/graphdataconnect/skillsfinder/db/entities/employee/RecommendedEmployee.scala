package com.microsoft.graphdataconnect.skillsfinder.db.entities.employee

import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.Employee
import kong.unirest.json.JSONArray

import scala.collection.JavaConverters._

case class RecommendedEmployee(employee_id: String,
                               name: String,
                               mail: String,
                               employee_role: String,
                               inferred_roles_json: String,
                               location: String,
                               about_me: String,
                               current_engagement: String,
                               department: String,
                               reports_to: String,
                               manager_email: String,
                               available_since: String,
                               linkedin_profile: String,
                               included_in_current_team: Boolean,
                               total: Int,
                               skills_json: String,
                               profile_picture: String) {

  def toEmployee(declaredSkills: List[String]): Employee = {
    Employee(
      id = employee_id,
      name = name,
      mail = mail.trim,
      role = employee_role,
      inferredRoles = parseInferredRolesJson(inferred_roles_json).filter(_.nonEmpty).map(_.trim.toLowerCase).distinct,
      location = location,
      about = about_me,
      relevantSkills = declaredSkills,
      inferredSkills = List.empty,
      declaredSkills = declaredSkills,
      domainToSkillMap = Map.empty[String, List[String]],
      highlightedTerms = List.empty,
      topics = List.empty,
      currentEngagement = current_engagement,
      department = department,
      reportsTo = reports_to,
      managerEmail = manager_email,
      availableSince = available_since,
      linkedInProfile = linkedin_profile,
      includedInCurrentTeam = Some(included_in_current_team),
      profilePicture = profile_picture
    )
  }

  private def parseInferredRolesJson(inferredRolesJson: String): List[String] = {
    val inferredRolesJsonArray = new JSONArray(inferredRolesJson)
    inferredRolesJsonArray.toList.asScala.toList.asInstanceOf[List[String]]
  }

}
