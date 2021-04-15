package com.microsoft.graphdataconnect.skillsfinder.models

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.SearchCriterion

case class SearchCriteriaSettings(searchCriteria: List[SearchCriterion],
                                  useReceivedEmailsContent: Boolean)
