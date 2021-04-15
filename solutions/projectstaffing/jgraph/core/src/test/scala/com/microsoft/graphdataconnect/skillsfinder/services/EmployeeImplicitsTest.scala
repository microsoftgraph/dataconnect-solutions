package com.microsoft.graphdataconnect.skillsfinder.services

import com.microsoft.graphdataconnect.skillsfinder.models.TaxonomyType
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.Employee
import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.recommendation.EmployeeRecommendation
import com.microsoft.graphdataconnect.skillsfinder.service.EmployeeSearchService.EmployeeImplicits
import org.junit.Assert
import org.junit.jupiter.api.Test

import scala.collection.immutable.ListMap

class EmployeeImplicitsTest {
  val employee = Employee(id = "1",
    name = "John Doe",
    mail = "jdoe@example.com",
    role = "UX Developer",
    inferredRoles = List("senior software developer", "software developer"),
    location = "US",
    about = null,
    relevantSkills = null,
    inferredSkills = null,
    declaredSkills = List("vue", "javascript", "user experience design", "ui development", "software engineering"),
    domainToSkillMap = null,
    highlightedTerms = null,
    topics = null,
    currentEngagement = null,
    reportsTo = null,
    managerEmail = null,
    availableSince = null,
    linkedInProfile = null,
    includedInCurrentTeam = None,
    profilePicture = null
  )

  // Simulating a recommendation obtained by searching for "experience"
  val employeeRecommendation = EmployeeRecommendation(
    email = "jdoe@example.com",
    score = 1,
    relevantSkills = List("user experience design", "software engineering", "ui development"),
    profileSkills = List("vue", "user experience design", "ui development", "javascript", "software engineering"),
    inferredSkills = List(),
    inferredSkillsFromTaxonomy = Map("facilities" -> List("process", "team", "leadership", "business", "quality"),
      "finance" -> List.empty,
      "human_relations" -> List("health", "company", "professional", "human", "talent management"),
      "legal" -> List("legal", "advice", "planning", "manager", "accounting"),
      "oilgas" -> List("technical support", "audit", "cost estimates", "analysis"),
      "software" -> List("internship", "hiring", "motivation", "programmer-skills", "professional-experience")),
    highlightedTerms = List("experience", "user experience design", "professional-experience")
  )


  @Test
  def testEnhanceEmployee_explicitRequiredTaxonomyOrder(): Unit = {
    val requiredTaxonomyOrder = List("software", "human_relations", "finance", "legal", "facilities", "oilgas")

    val processedEmployee = employee.enhancedWithInferredInformationLists(employeeRecommendation, Some(requiredTaxonomyOrder))


    val expectedDomainToSkillsMap: ListMap[String, List[String]] = ListMap(
      "software" -> List("internship", "hiring", "motivation", "programmer-skills", "professional-experience"),
      "human_relations" -> List("health", "company", "professional", "human", "talent management"),
      "finance" -> List.empty,
      "legal" -> List("legal", "advice", "planning", "manager", "accounting"),
      "facilities" -> List("process", "team", "leadership", "business", "quality"),
      "oilgas" -> List("technical support", "audit", "cost estimates", "analysis")
    )

    Assert.assertTrue(processedEmployee.relevantSkills == employeeRecommendation.relevantSkills)
    Assert.assertTrue(processedEmployee.inferredSkills == employeeRecommendation.inferredSkills)
    Assert.assertTrue(processedEmployee.declaredSkills == employee.declaredSkills)
    Assert.assertTrue(processedEmployee.highlightedTerms == employeeRecommendation.highlightedTerms)

    Assert.assertTrue(processedEmployee.domainToSkillMap.keys.toList == requiredTaxonomyOrder)
    Assert.assertTrue(processedEmployee.domainToSkillMap == expectedDomainToSkillsMap)
  }

  @Test
  def testEnhanceEmployee_noRequiredTaxonomyOrder(): Unit = {
    val processedEmployee = employee.enhancedWithInferredInformationLists(employeeRecommendation, None)

    val expectedDomainToSkillsMap: ListMap[String, List[String]] = ListMap(
      "software" -> List("internship", "hiring", "motivation", "programmer-skills", "professional-experience"),
      "facilities" -> List("process", "team", "leadership", "business", "quality"),
      "finance" -> List.empty,
      "human_relations" -> List("health", "company", "professional", "human", "talent management"),
      "legal" -> List("legal", "advice", "planning", "manager", "accounting"),
      "oilgas" -> List("technical support", "audit", "cost estimates", "analysis")
    )
    val defaultTaxonomyOrder = TaxonomyType.values.toList.map(_.toString)
    val expectedTaxonomyOrder = defaultTaxonomyOrder.filter(expectedDomainToSkillsMap.keys.toSet.contains(_))

    Assert.assertTrue(processedEmployee.relevantSkills == employeeRecommendation.relevantSkills)
    Assert.assertTrue(processedEmployee.inferredSkills == employeeRecommendation.inferredSkills)
    Assert.assertTrue(processedEmployee.declaredSkills == employee.declaredSkills)
    Assert.assertTrue(processedEmployee.highlightedTerms == employeeRecommendation.highlightedTerms)

    Assert.assertTrue(processedEmployee.domainToSkillMap.keys.toList == expectedTaxonomyOrder)
    Assert.assertTrue(processedEmployee.domainToSkillMap == expectedDomainToSkillsMap)
  }

  @Test
  def testEnhanceEmployee_emptyRequiredTaxonomyOrder(): Unit = {
    val processedEmployee = employee.enhancedWithInferredInformationLists(employeeRecommendation, Some(List()))

    val expectedDomainToSkillsMap: ListMap[String, List[String]] = ListMap(
      "software" -> List("internship", "hiring", "motivation", "programmer-skills", "professional-experience"),
      "facilities" -> List("process", "team", "leadership", "business", "quality"),
      "finance" -> List.empty,
      "human_relations" -> List("health", "company", "professional", "human", "talent management"),
      "legal" -> List("legal", "advice", "planning", "manager", "accounting"),
      "oilgas" -> List("technical support", "audit", "cost estimates", "analysis")
    )
    val defaultTaxonomyOrder = TaxonomyType.values.toList.map(_.toString)
    val expectedTaxonomyOrder = defaultTaxonomyOrder.filter(expectedDomainToSkillsMap.keys.toSet.contains(_))

    Assert.assertTrue(processedEmployee.relevantSkills == employeeRecommendation.relevantSkills)
    Assert.assertTrue(processedEmployee.inferredSkills == employeeRecommendation.inferredSkills)
    Assert.assertTrue(processedEmployee.declaredSkills == employee.declaredSkills)
    Assert.assertTrue(processedEmployee.highlightedTerms == employeeRecommendation.highlightedTerms)

    Assert.assertTrue(processedEmployee.domainToSkillMap.keys.toList == expectedTaxonomyOrder)
    Assert.assertTrue(processedEmployee.domainToSkillMap == expectedDomainToSkillsMap)
  }

}
