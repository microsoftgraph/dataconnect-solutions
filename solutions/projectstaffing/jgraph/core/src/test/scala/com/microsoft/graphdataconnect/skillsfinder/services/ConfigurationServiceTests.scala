/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.services

import com.microsoft.graphdataconnect.model.configs.ConfigurationTypes
import com.microsoft.graphdataconnect.skillsfinder.db.entities.Configuration
import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.DataSourceType._
import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings._
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.ConfigurationRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.settings.SearchSettingsRepository
import com.microsoft.graphdataconnect.skillsfinder.models.FilterType._
import com.microsoft.graphdataconnect.skillsfinder.models.configs.{AzureBlobStorageCredential, DatabricksConfiguration, OAuthAzureActiveDirectoryConfiguration}
import com.microsoft.graphdataconnect.skillsfinder.models.{DataSourceSettings, SearchResultsFilteringSettings, SearchSettingsRequestResponse}
import com.microsoft.graphdataconnect.skillsfinder.service.ConfigurationService
import javax.persistence.EntityManagerFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.{Assert, BeforeClass, Test}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}

import scala.compat.java8.OptionConverters._

class ConfigurationServiceTests {
  val dataSourceSettings = DataSourceSettings(DataSourceType.values().toList, false)

  val sc1 = SearchCriterion(SearchCriterionType.PROFILE_SKILLS, true)
  val sc2 = SearchCriterion(SearchCriterionType.PROFILE_ABOUT_ME, true)
  val sc3 = SearchCriterion(SearchCriterionType.PROFILE_TOPICS, true)
  val sc4 = SearchCriterion(SearchCriterionType.EMAIL_CONTENT, true)
  val sc5 = SearchCriterion(SearchCriterionType.EMAIL_CONTENT_LEMMATIZED, true)
  val sc6 = SearchCriterion(SearchCriterionType.DE_PROFILE, true)
  val sc7 = SearchCriterion(SearchCriterionType.DE_EMAIL_CONTENT, true)
  val sc8 = SearchCriterion(SearchCriterionType.RECEIVED_EMAIL_CONTENT, true)
  val sc9 = SearchCriterion(SearchCriterionType.DE_RECEIVED_EMAIL_CONTENT, true)

  val m365SearchFilters: SearchFiltersByDataSource = SearchFiltersByDataSource(M365, List(SearchFilterConfig(M365_DEPARTMENT, isActive = true),
    SearchFilterConfig(M365_COUNTRY, isActive = true),
    SearchFilterConfig(M365_STATE, isActive = true),
    SearchFilterConfig(M365_CITY, isActive = true)))
  val hrDataSearchFilters: SearchFiltersByDataSource = SearchFiltersByDataSource(HRData, List(SearchFilterConfig(HR_DATA_LOCATION, isActive = true)))

  /*
   * Databricks
   */
  @Test
  def testGetDatabricksConfiguration_WithSecret(): Unit = {
    // Arrange
    val storedConfiguration = new Configuration()
    storedConfiguration.`type` = ConfigurationTypes.Databricks
    storedConfiguration.configs = Map(
      "address" -> "addressVal",
      "apiToken" -> "apiTokenVal",
      "orgId" -> "orgIdVal",
      "clusterId" -> "clusterIdVal",
      "port" -> "portVal"
    )
    when(ConfigurationServiceTests.mockConfigurationRepository.findById(any[String])).thenReturn(Some(storedConfiguration).asJava)

    // Act
    val configurationOpt: Option[DatabricksConfiguration] = ConfigurationServiceTests.service.getDatabricksConfiguration(true)

    // Assert
    assertThat(configurationOpt.isDefined)
    val configuration = configurationOpt.get
    assertThat(configuration.address).isEqualTo(storedConfiguration.configs("address"))
    assertThat(configuration.apiToken).isEqualTo(storedConfiguration.configs("apiToken"))
    assertThat(configuration.orgId).isEqualTo(storedConfiguration.configs("orgId"))
    assertThat(configuration.clusterId).isEqualTo(storedConfiguration.configs("clusterId"))
    assertThat(configuration.port).isEqualTo(storedConfiguration.configs("port"))
  }

  @Test
  def testGetDatabricksConfiguration_WithoutSecret(): Unit = {
    // Arrange
    val storedConfiguration = new Configuration()
    storedConfiguration.`type` = ConfigurationTypes.Databricks
    storedConfiguration.configs = Map(
      "address" -> "addressVal",
      "apiToken" -> "apiTokenVal",
      "orgId" -> "orgIdVal",
      "clusterId" -> "clusterIdVal",
      "port" -> "portVal"
    )
    when(ConfigurationServiceTests.mockConfigurationRepository.findById(any[String])).thenReturn(Some(storedConfiguration).asJava)

    // Act
    val configurationOpt: Option[DatabricksConfiguration] = ConfigurationServiceTests.service.getDatabricksConfiguration()

    // Assert
    assertThat(configurationOpt.isDefined)
    val configuration = configurationOpt.get
    assertThat(configuration.apiToken).isEmpty()
    assertThat(configuration.address).isEqualTo(storedConfiguration.configs("address"))
    assertThat(configuration.orgId).isEqualTo(storedConfiguration.configs("orgId"))
    assertThat(configuration.clusterId).isEqualTo(storedConfiguration.configs("clusterId"))
    assertThat(configuration.port).isEqualTo(storedConfiguration.configs("port"))
  }

  @Test
  def testCreateOrUpdateDatabricksConfiguration(): Unit = {
    // Arrange
    val databricksConfig: DatabricksConfiguration = DatabricksConfiguration("https://addressVal", "apiTokenVal", "orgIdVal", "clusterIdVal", "portVal")
    val config = new Configuration()
    config.`type` = ConfigurationTypes.Databricks
    config.configs = Map(
      "address" -> "https://addressVal",
      "apiToken" -> "apiTokenVal",
      "orgId" -> "orgIdVal",
      "clusterId" -> "clusterIdVal",
      "port" -> "portVal"
    )

    when(ConfigurationServiceTests.mockConfigurationRepository.save(any[Configuration])).thenReturn(config)
    // Act
    val createdConfig: DatabricksConfiguration = ConfigurationServiceTests.service.createOrUpdateDatabricksConfiguration(databricksConfig)

    // Assert
    assertThat(databricksConfig.address).isEqualTo(createdConfig.address)
    assertThat(databricksConfig.apiToken).isEqualTo(createdConfig.apiToken)
    assertThat(databricksConfig.orgId).isEqualTo(createdConfig.orgId)
    assertThat(databricksConfig.clusterId).isEqualTo(createdConfig.clusterId)
    assertThat(databricksConfig.port).isEqualTo(createdConfig.port)
  }


  /*
   * Blob Storage
   */

  @Test
  def testGetBlobStorageAccountDetails_WithSecret(): Unit = {
    // Arrange
    val storedConfiguration = new Configuration()
    storedConfiguration.`type` = ConfigurationTypes.AzureBlobStorage
    storedConfiguration.configs = Map(
      "storageAccount" -> "storageAccountVal",
      "storageKey" -> "storageKeyVal"
    )
    when(ConfigurationServiceTests.mockConfigurationRepository.findById(any[String])).thenReturn(Some(storedConfiguration).asJava)

    // Act
    val configurationOpt: Option[AzureBlobStorageCredential] = ConfigurationServiceTests.service.getBlobStorageAccountDetails(true)

    // Assert
    assertThat(configurationOpt.isDefined)
    val configuration = configurationOpt.get
    assertThat(configuration.storageAccount).isEqualTo(storedConfiguration.configs("storageAccount"))
    assertThat(configuration.storageKey).isEqualTo(storedConfiguration.configs("storageKey"))
  }

  @Test
  def testGetBlobStorageAccountDetails_WithoutSecret(): Unit = {
    // Arrange
    val storedConfiguration = new Configuration()
    storedConfiguration.`type` = ConfigurationTypes.AzureBlobStorage
    storedConfiguration.configs = Map(
      "storageAccount" -> "storageAccountVal",
      "storageKey" -> "storageKeyVal"
    )
    when(ConfigurationServiceTests.mockConfigurationRepository.findById(any[String])).thenReturn(Some(storedConfiguration).asJava)

    // Act
    val configurationOpt: Option[AzureBlobStorageCredential] = ConfigurationServiceTests.service.getBlobStorageAccountDetails()

    // Assert
    assertThat(configurationOpt.isDefined)
    val configuration = configurationOpt.get
    assertThat(configuration.storageKey).isEmpty()
    assertThat(configuration.storageAccount).isEqualTo(storedConfiguration.configs("storageAccount"))
  }

  @Test
  def testCreateOrUpdateBlobStorageDetails(): Unit = {
    // Arrange
    val azureBlobStorageCredential: AzureBlobStorageCredential = AzureBlobStorageCredential("storageAccountVal", "storageKeyVal")
    val config = new Configuration()
    config.`type` = ConfigurationTypes.AzureBlobStorage
    config.configs = Map(
      "storageAccount" -> "storageAccountVal",
      "storageKey" -> "storageKeyVal"
    )

    when(ConfigurationServiceTests.mockConfigurationRepository.save(any[Configuration])).thenReturn(config)
    // Act
    val createdConfig: AzureBlobStorageCredential = ConfigurationServiceTests.service.createOrUpdateBlobStorageDetails(azureBlobStorageCredential)

    // Assert
    assertThat(azureBlobStorageCredential.storageAccount).isEqualTo(createdConfig.storageAccount)
    assertThat(azureBlobStorageCredential.storageKey).isEqualTo(createdConfig.storageKey)
  }

  /*
   * Active Directory
   */

  @Test
  def testGetAzureActiveDirectoryConfiguration_WithSecret(): Unit = {
    // Arrange
    val storedConfiguration = new Configuration()
    storedConfiguration.`type` = ConfigurationTypes.AzureActiveDirectory
    storedConfiguration.configs = Map(
      "clientId" -> "clientIdVal",
      "tenantId" -> "tenantIdVal",
      "clientSecret" -> "clientSecretVal"
    )

    when(ConfigurationServiceTests.mockConfigurationRepository.findById(any[String])).thenReturn(Some(storedConfiguration).asJava)

    // Act
    val configurationOpt: Option[OAuthAzureActiveDirectoryConfiguration] = ConfigurationServiceTests.service.getAzureActiveDirectoryConfiguration(true)

    // Assert
    assertThat(configurationOpt.isDefined)
    val configuration = configurationOpt.get
    assertThat(configuration.clientId).isEqualTo(storedConfiguration.configs("clientId"))
    assertThat(configuration.tenantId).isEqualTo(storedConfiguration.configs("tenantId"))
    assertThat(configuration.clientSecret).isEqualTo(storedConfiguration.configs("clientSecret"))
  }

  @Test
  def testGetAzureActiveDirectoryConfiguration_WithoutSecret(): Unit = {
    // Arrange
    val storedConfiguration = new Configuration()
    storedConfiguration.`type` = ConfigurationTypes.AzureActiveDirectory
    storedConfiguration.configs = Map(
      "clientId" -> "clientIdVal",
      "tenantId" -> "tenantIdVal",
      "clientSecret" -> "clientSecretVal"
    )

    when(ConfigurationServiceTests.mockConfigurationRepository.findById(any[String])).thenReturn(Some(storedConfiguration).asJava)

    // Act
    val configurationOpt: Option[OAuthAzureActiveDirectoryConfiguration] = ConfigurationServiceTests.service.getAzureActiveDirectoryConfiguration()

    // Assert
    assertThat(configurationOpt.isDefined)
    val configuration = configurationOpt.get
    assertThat(configuration.clientSecret).isEmpty()
    assertThat(configuration.clientId).isEqualTo(storedConfiguration.configs("clientId"))
    assertThat(configuration.tenantId).isEqualTo(storedConfiguration.configs("tenantId"))
  }

  @Test
  def testCreateOrUpdateAzureActiveDirectoryDetails(): Unit = {
    // Arrange
    val adConfig: OAuthAzureActiveDirectoryConfiguration = OAuthAzureActiveDirectoryConfiguration("clientIdVal", "tenantIdVal", "clientSecretVal")
    val config = new Configuration()
    config.`type` = ConfigurationTypes.AzureActiveDirectory
    config.configs = Map(
      "clientId" -> "clientIdVal",
      "tenantId" -> "tenantIdVal",
      "clientSecret" -> "clientSecretVal"
    )

    when(ConfigurationServiceTests.mockConfigurationRepository.save(any[Configuration])).thenReturn(config)
    // Act
    val createdConfig: OAuthAzureActiveDirectoryConfiguration = ConfigurationServiceTests.service.createOrUpdateAzureActiveDirectoryDetails(adConfig)

    // Assert
    assertThat(adConfig.clientId).isEqualTo(createdConfig.clientId)
    assertThat(adConfig.tenantId).isEqualTo(createdConfig.tenantId)
    assertThat(adConfig.clientSecret).isEqualTo(createdConfig.clientSecret)
  }


  @Test
  def testSearchSettings(): Unit = {
    val searchSettings1 = new SearchSettingsRequestResponse(dataSourceSettings,
      List(sc1, sc2, sc3, sc4),
      List(m365SearchFilters, hrDataSearchFilters),
      useReceivedEmailsContent = true,
      freshness = 10,
      freshnessEnabled = true,
      volume = 20,
      volumeEnabled = true,
      relevanceScore = 70,
      relevanceScoreEnabled = true,
      freshnessBeginDate = null,
      freshnessBeginDateEnabled = true,
      includedEmailDomains = List(),
      includedEmailDomainsEnabled = true,
      excludedEmailDomains = List(),
      excludedEmailDomainsEnabled = true)

    ConfigurationServiceTests.service.saveSearchSettings(searchSettings1, "user@company.com")

    val result = ConfigurationServiceTests.service.getSearchSettings("user@company.com")
    println(result)
  }

  @Test
  def testDefaultSearchResultsFilterSettingsWhenM365IsPrimarySource(): Unit = {
    val result: SearchResultsFilteringSettings = ConfigurationServiceTests.service.getDefaultSearchResultsFilteringSettings(true)
    assertThat(result.searchResultsFilters.head.dataSource == DataSourceType.M365)
    val m365SearchFilterSettings = result.searchResultsFilters.head.filters
    //    Assert.assertEquals(List(M365_COUNTRY, M365_STATE, M365_CITY, M365_OFFICE_LOCATION, M365_COMPANY_NAME, M365_DEPARTMENT, M365_ROLE), m365SearchFilterSettings.map(_.filterType))
    Assert.assertEquals(List(M365_COUNTRY, M365_STATE, M365_CITY, M365_DEPARTMENT, M365_ROLE), m365SearchFilterSettings.map(_.filterType))
    //    Assert.assertEquals(List(true, true, true, true, true, true, true), m365SearchFilterSettings.map(_.isActive))
    Assert.assertEquals(List(true, true, true, true, true), m365SearchFilterSettings.map(_.isActive))
    val hrDataSearchFilterSettings = result.searchResultsFilters.tail.head.filters
    Assert.assertEquals(List(HR_DATA_LOCATION, HR_DATA_ROLE), hrDataSearchFilterSettings.map(_.filterType))
    Assert.assertEquals(List(false, false), hrDataSearchFilterSettings.map(_.isActive))
  }

  @Test
  def testDefaultSearchResultsFilterSettingsWhenHRDataIsPrimarySource(): Unit = {
    val result: SearchResultsFilteringSettings = ConfigurationServiceTests.service.getDefaultSearchResultsFilteringSettings(false)
    assertThat(result.searchResultsFilters.head.dataSource == DataSourceType.M365)
    val m365SearchFilterSettings = result.searchResultsFilters.head.filters
    //    Assert.assertEquals(List(M365_COUNTRY, M365_STATE, M365_CITY, M365_OFFICE_LOCATION, M365_COMPANY_NAME, M365_DEPARTMENT, M365_ROLE), m365SearchFilterSettings.map(_.filterType))
    Assert.assertEquals(List(M365_COUNTRY, M365_STATE, M365_CITY, M365_DEPARTMENT, M365_ROLE), m365SearchFilterSettings.map(_.filterType))
    //    Assert.assertEquals(List(false, false, false, false, false, false, false), m365SearchFilterSettings.map(_.isActive))
    Assert.assertEquals(List(false, false, false, false, false), m365SearchFilterSettings.map(_.isActive))
    val hrDataSearchFilterSettings = result.searchResultsFilters.tail.head.filters
    Assert.assertEquals(List(HR_DATA_LOCATION, HR_DATA_ROLE), hrDataSearchFilterSettings.map(_.filterType))
    Assert.assertEquals(List(true, true), hrDataSearchFilterSettings.map(_.isActive))
  }

  @Test
  def testSearchSettings_duplicateSearchCriterion(): Unit = {
    val duplicateSearchCriterion = sc1
    val searchSettings1 = new SearchSettingsRequestResponse(dataSourceSettings,
      List(sc1, sc2, sc3, sc4, duplicateSearchCriterion),
      List(m365SearchFilters, hrDataSearchFilters),
      useReceivedEmailsContent = true,
      freshness = 10,
      freshnessEnabled = true,
      volume = 20,
      volumeEnabled = true,
      relevanceScore = 70,
      relevanceScoreEnabled = true,
      freshnessBeginDate = null,
      freshnessBeginDateEnabled = true,
      includedEmailDomains = List(),
      includedEmailDomainsEnabled = true,
      excludedEmailDomains = List(),
      excludedEmailDomainsEnabled = true)

    ConfigurationServiceTests.assertThrows(
      () => ConfigurationServiceTests.service.saveSearchSettings(searchSettings1, "user@company.com"),
      classOf[IllegalArgumentException], "There are duplicate types in search criterion types list!",
      "Expected saveSearchSettings to throw IllegalArgumentException, but it didn't!")
  }

  @Test
  def testSearchSettings_missingSearchCriterion(): Unit = {
    val searchSettings2 = new SearchSettingsRequestResponse(dataSourceSettings,
      List(sc1, sc2, sc4, sc5),
      List(m365SearchFilters, hrDataSearchFilters),
      useReceivedEmailsContent = true,
      freshness = 10,
      freshnessEnabled = true,
      volume = 20,
      volumeEnabled = true,
      relevanceScore = 70,
      relevanceScoreEnabled = true,
      freshnessBeginDate = null,
      freshnessBeginDateEnabled = true,
      includedEmailDomains = List(),
      includedEmailDomainsEnabled = true,
      excludedEmailDomains = List(),
      excludedEmailDomainsEnabled = true)

    ConfigurationServiceTests.assertThrows(
      () => ConfigurationServiceTests.service.saveSearchSettings(searchSettings2, "user@company.com"),
      classOf[IllegalArgumentException], "Missing search criterion types!",
      "Expected saveSearchSettings to throw IllegalArgumentException, but it didn't!")
  }

  @Test
  def testSearchSettings_defaultSearchCriteria(): Unit = {
    val expectedSearchCriteria = List(sc1, sc2, sc3, sc4)

    val defaultConfig = ConfigurationServiceTests.service.getDefaultSearchCriteriaSettings()

    assert(defaultConfig.searchCriteria == expectedSearchCriteria)
  }
}

object ConfigurationServiceTests {
  var mockConfigurationRepository: ConfigurationRepository = _
  var mockSearchSettingsRepository: SearchSettingsRepository = _
  var emf: EntityManagerFactory = _
  var service: ConfigurationService = _

  @BeforeClass
  def setup() {
    mockConfigurationRepository = mock(classOf[ConfigurationRepository])
    mockSearchSettingsRepository = mock(classOf[SearchSettingsRepository])
    emf = mock(classOf[EntityManagerFactory])
    service = new ConfigurationService(mockConfigurationRepository, mockSearchSettingsRepository, emf)
  }

  def assertThrows(f: () => Unit, expectedExceptionType: Class[_], expectedExceptionMessage: String, assertionFailureMessage: String): Unit = {
    try {
      f()
    } catch {
      case e: Throwable =>
        if (expectedExceptionType.getTypeName.equals(e.getClass.getTypeName) &&
          expectedExceptionMessage.equals(e.getMessage)) return

    }
    throw new AssertionError(assertionFailureMessage)
  }
}
