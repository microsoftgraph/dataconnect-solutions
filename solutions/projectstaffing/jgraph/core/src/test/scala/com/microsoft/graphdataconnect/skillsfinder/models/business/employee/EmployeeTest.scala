/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.employee

import org.junit.Assert
import org.junit.jupiter.api.Test

class EmployeeTest {
  val employee = Employee(id = "1",
    name = "John Doe",
    mail = "jdoe@example.com",
    role = "Engineer",
    inferredRoles = List("Programmer", "Manager"),
    location = "US",
    about = null,
    relevantSkills = List("java", "scala"),
    inferredSkills = List("java", "scala"),
    declaredSkills = List("java", "scala"),
    domainToSkillMap = Map("Domain1" -> List("term1", "term2"), "Domain2" -> List("term3", "term4")),
    highlightedTerms = List("java"),
    topics = null,
    currentEngagement = null,
    reportsTo = null,
    managerEmail = null,
    availableSince = null,
    linkedInProfile = null,
    includedInCurrentTeam = None,
    profilePicture = null
  )

  @Test
  def dropEmptyElementsFromLists_nullLists(): Unit = {
    val testEmployee = employee.copy(
      inferredRoles = null,
      relevantSkills = null,
      inferredSkills = null,
      declaredSkills = null,
      highlightedTerms = null
    )

    val processedEmployee = Employee.dropEmptyElementsFromLists(testEmployee)

    Assert.assertTrue(processedEmployee.inferredRoles == List())
    Assert.assertTrue(processedEmployee.relevantSkills == List())
    Assert.assertTrue(processedEmployee.inferredSkills == List())
    Assert.assertTrue(processedEmployee.declaredSkills == List())
    Assert.assertTrue(processedEmployee.highlightedTerms == List())
  }

  @Test
  def dropEmptyElementsFromLists_nilLists(): Unit = {
    val testEmployee = employee.copy(
      inferredRoles = Nil,
      relevantSkills = Nil,
      inferredSkills = Nil,
      declaredSkills = Nil,
      highlightedTerms = Nil
    )

    val processedEmployee = Employee.dropEmptyElementsFromLists(testEmployee)

    Assert.assertTrue(processedEmployee.inferredRoles == List())
    Assert.assertTrue(processedEmployee.relevantSkills == List())
    Assert.assertTrue(processedEmployee.inferredSkills == List())
    Assert.assertTrue(processedEmployee.declaredSkills == List())
    Assert.assertTrue(processedEmployee.highlightedTerms == List())
  }

  @Test
  def dropEmptyElementsFromLists_emptyLists(): Unit = {
    val testEmployee = employee.copy(
      inferredRoles = List(),
      relevantSkills = List(),
      inferredSkills = List(),
      declaredSkills = List(),
      highlightedTerms = List()
    )

    val processedEmployee = Employee.dropEmptyElementsFromLists(testEmployee)

    Assert.assertTrue(processedEmployee.inferredRoles == List())
    Assert.assertTrue(processedEmployee.relevantSkills == List())
    Assert.assertTrue(processedEmployee.inferredSkills == List())
    Assert.assertTrue(processedEmployee.declaredSkills == List())
    Assert.assertTrue(processedEmployee.highlightedTerms == List())
  }

  @Test
  def dropEmptyElementsFromLists_listsWithEmptyString(): Unit = {
    val testEmployee = employee.copy(
      inferredRoles = List(""),
      relevantSkills = List(""),
      inferredSkills = List(""),
      declaredSkills = List(""),
      highlightedTerms = List("")
    )

    val processedEmployee = Employee.dropEmptyElementsFromLists(testEmployee)

    Assert.assertTrue(processedEmployee.inferredRoles == List())
    Assert.assertTrue(processedEmployee.relevantSkills == List())
    Assert.assertTrue(processedEmployee.inferredSkills == List())
    Assert.assertTrue(processedEmployee.declaredSkills == List())
    Assert.assertTrue(processedEmployee.highlightedTerms == List())
  }

  @Test
  def dropEmptyElementsFromLists_listsStringsOfSpaces(): Unit = {
    val testEmployee = employee.copy(
      inferredRoles = List("  "),
      relevantSkills = List("  "),
      inferredSkills = List("  "),
      declaredSkills = List("  "),
      highlightedTerms = List("  ")
    )

    val processedEmployee = Employee.dropEmptyElementsFromLists(testEmployee)

    Assert.assertTrue(processedEmployee.inferredRoles == List())
    Assert.assertTrue(processedEmployee.relevantSkills == List())
    Assert.assertTrue(processedEmployee.inferredSkills == List())
    Assert.assertTrue(processedEmployee.declaredSkills == List())
    Assert.assertTrue(processedEmployee.highlightedTerms == List())
  }

  @Test
  def dropEmptyElementsFromLists_listsWithNull(): Unit = {
    val testEmployee = employee.copy(
      inferredRoles = List(null),
      relevantSkills = List(null),
      inferredSkills = List(null),
      declaredSkills = List(null),
      highlightedTerms = List(null)
    )

    val processedEmployee = Employee.dropEmptyElementsFromLists(testEmployee)

    Assert.assertTrue(processedEmployee.inferredRoles == List())
    Assert.assertTrue(processedEmployee.relevantSkills == List())
    Assert.assertTrue(processedEmployee.inferredSkills == List())
    Assert.assertTrue(processedEmployee.declaredSkills == List())
    Assert.assertTrue(processedEmployee.highlightedTerms == List())
  }

  @Test
  def dropEmptyElementsFromLists_listsWithSeveralEmptyStrings(): Unit = {
    val testEmployee = employee.copy(
      inferredRoles = List("", "Programmer", "", "", "Manager", ""),
      relevantSkills = List("", "java", "", "", "scala", ""),
      inferredSkills = List("", "java", "", "", "scala", ""),
      declaredSkills = List("", "java", "", "", "scala", ""),
      highlightedTerms = List("", "java", "", "", "scala", "")
    )


    val processedEmployee = Employee.dropEmptyElementsFromLists(testEmployee)

    Assert.assertTrue(processedEmployee.inferredRoles == List("Programmer", "Manager"))
    Assert.assertTrue(processedEmployee.relevantSkills == List("java", "scala"))
    Assert.assertTrue(processedEmployee.inferredSkills == List("java", "scala"))
    Assert.assertTrue(processedEmployee.declaredSkills == List("java", "scala"))
    Assert.assertTrue(processedEmployee.highlightedTerms == List("java", "scala"))
  }

  @Test
  def dropEmptyElementsFromLists_mixedLists(): Unit = {
    val testEmployee = employee.copy(
      inferredRoles = List(null, "", "Programmer", "", null, "", null, "Manager", null, ""),
      relevantSkills = List(null, "", "java", "", null, "", null, "scala", null, ""),
      inferredSkills = List(null, "", "java", "", null, "", null, "scala", null, ""),
      declaredSkills = List(null, "", "java", "", null, "", null, "scala", null, ""),
      highlightedTerms = List(null, "", "java", "", null, "", null, "scala", null, "")
    )


    val processedEmployee = Employee.dropEmptyElementsFromLists(testEmployee)

    Assert.assertTrue(processedEmployee.inferredRoles == List("Programmer", "Manager"))
    Assert.assertTrue(processedEmployee.relevantSkills == List("java", "scala"))
    Assert.assertTrue(processedEmployee.inferredSkills == List("java", "scala"))
    Assert.assertTrue(processedEmployee.declaredSkills == List("java", "scala"))
    Assert.assertTrue(processedEmployee.highlightedTerms == List("java", "scala"))
  }
}
