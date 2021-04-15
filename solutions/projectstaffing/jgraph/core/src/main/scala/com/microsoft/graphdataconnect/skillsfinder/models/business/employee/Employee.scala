package com.microsoft.graphdataconnect.skillsfinder.models.business.employee

import com.microsoft.graphdataconnect.skillsfinder.utils.SeqUtils.ListImplicits
import com.microsoft.graphdataconnect.skillsfinder.utils.StringUtils.StringImplicits

// TODO remove the nulls. Instead use proper values in all constructor calls
case class Employee(id: String,
                    name: String,
                    mail: String,
                    role: String,
                    inferredRoles: List[String],
                    location: String,
                    about: String = null,
                    relevantSkills: List[String],
                    inferredSkills: List[String] = null,
                    declaredSkills: List[String],
                    domainToSkillMap: Map[String, List[String]],
                    highlightedTerms: List[String] = null,
                    topics: List[String] = null,
                    currentEngagement: String = null,
                    department: String = null,
                    reportsTo: String = null,
                    managerEmail: String = null,
                    availableSince: String = null,
                    linkedInProfile: String,
                    includedInCurrentTeam: Option[Boolean] = None,
                    profilePicture: String)

object Employee {
  def dropEmptyElementsFromLists(employee: Employee): Employee = {
    employee.copy(
      inferredRoles = employee.inferredRoles.nullToNil.filterNot(_.isNullOrBlank),
      declaredSkills = employee.declaredSkills.nullToNil.filterNot(_.isNullOrBlank),
      relevantSkills = employee.relevantSkills.nullToNil.filterNot(_.isNullOrBlank),
      inferredSkills = employee.inferredSkills.nullToNil.filterNot(_.isNullOrBlank),
      highlightedTerms = employee.highlightedTerms.nullToNil.filterNot(_.isNullOrBlank)
    )
  }
}
