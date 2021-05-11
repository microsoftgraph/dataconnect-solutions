/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.employee.recommendation

import java.time.Instant

import scala.collection.mutable


case class EmployeeRecommendation(email: String,
                                  score: Double,
                                  relevantSkills: List[String], //the ones we thing the user is interested in
                                  profileSkills: List[String], // the profile skills
                                  inferredSkills: List[String], // the skills we infer
                                  inferredSkillsFromTaxonomy: Map[String, List[String]] = Map.empty[String, List[String]],
                                  highlightedTerms: List[String] = List.empty[String], //terms coming from search,
                                  recSourceDate: Instant = Instant.now(),
                                  foundReferenceText: String = "",
                                  foundReferenceField: String = "",
                                  upForRedeploymentDate: Option[Instant] = None,
                                  curatedAll: List[String] = List.empty[String]
                                 ) {
  def enhanceHighlightTerms(optimizedRequiredSkills: List[String]): EmployeeRecommendation = {
    val newHighlightedTerms = new scala.collection.mutable.ListBuffer[String]()

    highlightedTerms.foreach { p =>
      newHighlightedTerms ++= List(p)
    }
    val wordsToHighlight = (optimizedRequiredSkills ++ highlightedTerms).map(p => p.toLowerCase.trim)

    val allInferredSkillsPresent = inferredSkillsFromTaxonomy.map(p => p._2).flatten.toList

    wordsToHighlight.foreach {
      case required_skill =>
        if (required_skill.trim.split(" ").length >= 2 || required_skill.trim.split("-").length >= 2) {
          //first we parse the profile skills
          profileSkills.foreach {
            case skill =>
              if (skill.contains(required_skill)) {
                newHighlightedTerms ++= List(skill)
              }
          }
          allInferredSkillsPresent.foreach {
            skill =>
              if (skill.contains(required_skill)) {
                newHighlightedTerms ++= List(skill)
              }
          }
          relevantSkills.foreach {
            case skill =>
              if (skill.contains(required_skill)) {
                newHighlightedTerms ++= List(skill)
              }
          }
        }
        else {
          if (profileSkills.contains(required_skill) || allInferredSkillsPresent.contains(required_skill) || relevantSkills.contains(required_skill)) {
            newHighlightedTerms ++= List(required_skill)
          }


          val wordParts2 = List(" ", ".", "++", "-", "\\", "/", "+", "#").flatMap {
            symbol =>

              val prefix = symbol + required_skill
              val surround = symbol + required_skill + symbol
              val suffix = required_skill + symbol

              val isContains = if (required_skill.length > 1) true else false
              (List(prefix, surround, suffix) ++ List("[" + required_skill.trim + "]", "(" + required_skill.trim + ")", "{" + required_skill.trim + "}")).map(p => (p, isContains))
          }

          (profileSkills ++ allInferredSkillsPresent ++ relevantSkills).foreach {
            case skill =>
              wordParts2.foreach {
                case (wordPart, isContains) =>
                  if (isContains) {
                    if (skill.contains(wordPart)) {
                      newHighlightedTerms ++= List(skill)
                    }
                  }
                  else {
                    if (skill.startsWith(wordPart) || skill.endsWith(wordPart)) {
                      newHighlightedTerms ++= List(skill)
                    }
                  }
              }
          }
        }
    }

    val finalHighlightedTerms = newHighlightedTerms.toSet.toList
    EmployeeRecommendation(this.email,
      this.score,
      this.relevantSkills,
      this.profileSkills,
      this.inferredSkills,
      this.inferredSkillsFromTaxonomy,
      finalHighlightedTerms,
      this.recSourceDate,
      foundReferenceText = this.foundReferenceText,
      foundReferenceField = this.foundReferenceField,
      upForRedeploymentDate = this.upForRedeploymentDate)

  }

  def mailAddressFormatted(): String = {
    email.toLowerCase().trim
  }


}

object EmployeeRecommendation {

  def merge(employeeRecommendation: EmployeeRecommendation, otherEmployeeRecommendation: EmployeeRecommendation): EmployeeRecommendation = {
    val inferredSkills = mutable.LinkedHashSet(employeeRecommendation.inferredSkills: _*) ++ mutable.LinkedHashSet(otherEmployeeRecommendation.inferredSkills: _*)
    var inferredSkillsMapThis = employeeRecommendation.inferredSkillsFromTaxonomy
    val inferredSkillsMapOther = otherEmployeeRecommendation.inferredSkillsFromTaxonomy
    inferredSkillsMapOther.foreach {
      case (domain, listOfTerms) =>
        if (inferredSkillsMapThis.contains(domain) == false) {
          inferredSkillsMapThis += (domain -> listOfTerms)
        }
        else {
          val thisSkills = inferredSkillsMapThis(domain)
          val thisSkillsSet = thisSkills.toSet
          val skillsToAppend = new mutable.ListBuffer[String]()
          listOfTerms.foreach {
            otherTerm =>
              if (thisSkillsSet.contains(otherTerm) == false) {
                skillsToAppend += otherTerm
              }
          }
          inferredSkillsMapThis += (domain -> (thisSkills ++ skillsToAppend.toList))
        }
    }

    EmployeeRecommendation(employeeRecommendation.email,
      employeeRecommendation.score,
      employeeRecommendation.relevantSkills,
      employeeRecommendation.profileSkills,
      inferredSkills.toList,
      inferredSkillsMapThis,
      mutable.LinkedHashSet(employeeRecommendation.highlightedTerms ++ otherEmployeeRecommendation.highlightedTerms: _*).toList,
      employeeRecommendation.recSourceDate,
      foundReferenceText = employeeRecommendation.foundReferenceText,
      foundReferenceField = employeeRecommendation.foundReferenceField,
      upForRedeploymentDate = employeeRecommendation.upForRedeploymentDate.orElse(otherEmployeeRecommendation.upForRedeploymentDate)
    )

  }
}
