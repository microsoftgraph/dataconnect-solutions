package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.{SearchCriterion, SearchCriterionType, SearchSettings}
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.settings.SearchSettingsRepository
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.Autowired

class SearchSettingsRepositoryTest extends AbstractIntegrationTestBase {

  @Autowired
  var searchSettingsRepository: SearchSettingsRepository = _

  @Test
  def testThatFindByUserEmailWorksAsExpected(): Unit = {
    val sc1 = SearchCriterion(SearchCriterionType.PROFILE_SKILLS, true)
    val sc2 = SearchCriterion(SearchCriterionType.PROFILE_ABOUT_ME, true)
    val sc3 = SearchCriterion(SearchCriterionType.PROFILE_TOPICS, true)
    val sc4 = SearchCriterion(SearchCriterionType.EMAIL_CONTENT, true)
    val sc5 = SearchCriterion(SearchCriterionType.EMAIL_CONTENT_LEMMATIZED, true)
    val sc6 = SearchCriterion(SearchCriterionType.DE_PROFILE, true)
    val sc7 = SearchCriterion(SearchCriterionType.DE_EMAIL_CONTENT, true)
    val sc8 = SearchCriterion(SearchCriterionType.RECEIVED_EMAIL_CONTENT, true)
    val sc9 = SearchCriterion(SearchCriterionType.DE_RECEIVED_EMAIL_CONTENT, true)


    val searchSettings1 = new SearchSettings
    searchSettings1.userEmail = "user1@company.com"
    searchSettings1.freshness = 20
    searchSettings1.freshnessEnabled = true
    searchSettings1.volume = 50
    searchSettings1.volumeEnabled = true
    searchSettings1.relevanceScore = 30
    searchSettings1.relevanceScoreEnabled = true
    searchSettings1.freshnessBeginDate = null
    searchSettings1.freshnessBeginDateEnabled = true
    searchSettings1.includedEmailDomains = null
    searchSettings1.includedEmailDomainsEnabled = true
    searchSettings1.excludedEmailDomains = null
    searchSettings1.excludedEmailDomainsEnabled = true


    searchSettings1.searchCriteria = List(sc1, sc2, sc3, sc4, sc5, sc6, sc7, sc8, sc9)

    val searchSettings2 = new SearchSettings
    searchSettings2.userEmail = "user2@company.com"
    searchSettings2.freshness = 20
    searchSettings2.relevanceScore = 10
    searchSettings2.volume = 70
    searchSettings2.searchCriteria = List(sc9, sc2, sc3, sc4, sc5, sc6, sc7, sc8, sc1)

    searchSettingsRepository.save(searchSettings1)
    searchSettingsRepository.save(searchSettings2)

    val foundSettingsOpt: Option[SearchSettings] = Option(searchSettingsRepository.findByUserEmail(searchSettings1.userEmail).orElse(null))

    Assert.assertTrue(foundSettingsOpt.isDefined)

    val foundSettings = foundSettingsOpt.get
    Assert.assertTrue(foundSettings.searchCriteria == searchSettings1.searchCriteria)
    Assert.assertTrue(foundSettings.freshness == searchSettings1.freshness)
    Assert.assertTrue(foundSettings.freshnessEnabled == searchSettings1.freshnessEnabled)
    Assert.assertTrue(foundSettings.volume == searchSettings1.volume)
    Assert.assertTrue(foundSettings.volumeEnabled == searchSettings1.volumeEnabled)
    Assert.assertTrue(foundSettings.relevanceScore == searchSettings1.relevanceScore)
    Assert.assertTrue(foundSettings.relevanceScoreEnabled == searchSettings1.relevanceScoreEnabled)
    Assert.assertTrue(foundSettings.freshnessBeginDate == searchSettings1.freshnessBeginDate)
    Assert.assertTrue(foundSettings.freshnessBeginDateEnabled == searchSettings1.freshnessBeginDateEnabled)
    Assert.assertTrue(foundSettings.includedEmailDomains == searchSettings1.includedEmailDomains)
    Assert.assertTrue(foundSettings.includedEmailDomainsEnabled == searchSettings1.includedEmailDomainsEnabled)
    Assert.assertTrue(foundSettings.excludedEmailDomains == searchSettings1.excludedEmailDomains)
    Assert.assertTrue(foundSettings.excludedEmailDomainsEnabled == searchSettings1.excludedEmailDomainsEnabled)
  }

}
