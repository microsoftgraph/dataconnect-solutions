package com.microsoft.graphdataconnect.skillsfinder.models.business.employee

case class SkillSuggestion(suggestedSkill: String,
                           relatedTerms: Seq[String])
