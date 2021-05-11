/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.model.configs.{AzureSearchEmployeesIndexConfiguration, ConfigurationTypes}
import com.microsoft.graphdataconnect.skillsfinder.config.CachingConfig
import com.microsoft.graphdataconnect.skillsfinder.db.entities.Configuration
import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings._
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.ConfigurationRepository
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.settings.SearchSettingsRepository
import com.microsoft.graphdataconnect.skillsfinder.exceptions.AzureSearchIndexConfigurationMissing
import com.microsoft.graphdataconnect.skillsfinder.models._
import com.microsoft.graphdataconnect.skillsfinder.models.configs.{AzureBlobStorageCredential, DatabricksConfiguration, HDInsightConfiguration, OAuthAzureActiveDirectoryConfiguration}
import com.microsoft.graphdataconnect.utils.TimeUtils
import javax.persistence.{EntityManager, EntityManagerFactory}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.cache.annotation.{CachePut, Cacheable}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._


@Service
class ConfigurationService(@Autowired configurationRepository: ConfigurationRepository,
                           @Autowired searchSettingsRepository: SearchSettingsRepository,
                           @Autowired emf: EntityManagerFactory) {

  @Value("${app.dataSources.m365HasHighestPriority}")
  var m365HasHighestPriority: Boolean = true

  def isM365HighestPriorityDataSource(): Boolean = m365HasHighestPriority

  private val log: Logger = LoggerFactory.getLogger(classOf[ConfigurationService])

  def getHDInsightConfiguration(includeSecret: Boolean = false): Option[HDInsightConfiguration] = {
    configurationRepository.findById(ConfigurationTypes.HDInsight)
      .asScala
      .map { config =>
        if (includeSecret) {
          Configuration.toHDInsightConfiguration(config)
        } else {
          Configuration.toHDInsightConfiguration(config).copy(password = "")
        }
      }
  }

  def createOrUpdateHDInsightConfiguration(hdInsightConfiguration: HDInsightConfiguration): Unit = {
    log.info(s"Creating HDInsight config: ${hdInsightConfiguration.copy(password = "*")}")
    hdInsightConfiguration.validate()
    configurationRepository.save(Configuration(hdInsightConfiguration))
  }

  def deleteHDInsightConfiguration(): Unit = {
    log.info("Deleting HDInsight config")
    configurationRepository.deleteById(ConfigurationTypes.HDInsight)
  }


  def getDatabricksConfiguration(includeSecret: Boolean = false): Option[DatabricksConfiguration] = {
    configurationRepository.findById(ConfigurationTypes.Databricks)
      .asScala
      .map { config =>
        if (includeSecret) {
          Configuration.toDatabricksConfiguration(config)
        } else {
          Configuration.toDatabricksConfiguration(config).copy(apiToken = "")
        }
      }
  }

  def createOrUpdateDatabricksConfiguration(databricksConfig: DatabricksConfiguration): DatabricksConfiguration = {
    log.info(s"Creating Databricks config: ${databricksConfig.copy(apiToken = "*")}")
    databricksConfig.validate()
    Configuration.toDatabricksConfiguration(configurationRepository.save(Configuration(databricksConfig)))
  }

  def deleteDatabricksConfiguration(): Unit = {
    log.info("Deleting Databricks config")
    configurationRepository.deleteById(ConfigurationTypes.Databricks)
  }


  def getBlobStorageAccountDetails(includeSecret: Boolean = false): Option[AzureBlobStorageCredential] = {
    configurationRepository.findById(ConfigurationTypes.AzureBlobStorage)
      .asScala
      .map { config =>
        if (includeSecret) {
          Configuration.toAzureBlobStorageConfiguration(config)
        } else {
          Configuration.toAzureBlobStorageConfiguration(config).copy(storageKey = "")
        }
      }
  }

  def createOrUpdateBlobStorageDetails(blobStorageDetails: AzureBlobStorageCredential): AzureBlobStorageCredential = {
    log.info(s"Creating AzureBlobStorage config: ${blobStorageDetails.copy(storageKey = "*")}")
    blobStorageDetails.validate()
    Configuration.toAzureBlobStorageConfiguration(configurationRepository.save(Configuration(blobStorageDetails)))
  }

  def deleteBlobStorageDetails(): Unit = {
    log.info("Deleting AzureBlobStorage config")
    configurationRepository.deleteById(ConfigurationTypes.AzureBlobStorage)
  }


  def getAzureActiveDirectoryConfiguration(includeSecret: Boolean = false): Option[OAuthAzureActiveDirectoryConfiguration] = {
    configurationRepository.findById(ConfigurationTypes.AzureActiveDirectory)
      .asScala
      .map { config =>
        if (includeSecret) {
          Configuration.toAzureActiveDirectoryConfiguration(config)
        } else {
          Configuration.toAzureActiveDirectoryConfiguration(config).copy(clientSecret = "")
        }
      }
  }

  def createOrUpdateAzureActiveDirectoryDetails(azureActiveDirectory: OAuthAzureActiveDirectoryConfiguration): OAuthAzureActiveDirectoryConfiguration = {
    log.info(s"Creating AzureActiveDirectory config: ${azureActiveDirectory.copy(clientSecret = "*")}")
    Configuration.toAzureActiveDirectoryConfiguration(configurationRepository.save(Configuration(azureActiveDirectory)))
  }

  def deleteAzureActiveDirectoryDetails(): Unit = {
    log.info("Deleting AzureActiveDirectory config")
    configurationRepository.deleteById(ConfigurationTypes.AzureActiveDirectory)
  }

  def saveSearchSettings(searchSettingsRequest: SearchSettingsRequestResponse, userId: String): Unit = {
    val searchSettings = SearchSettings(searchSettingsRequest, userId)

    // Perform validation
    val dataSourcesPriority = searchSettingsRequest.dataSourceSettings.dataSourcesPriority
    val searchCriterionTypes: Seq[SearchCriterionType] = searchSettings.searchCriteria.map(searchCriterion => searchCriterion.searchCriterionType)
    if (!dataSourcesPriority.toSet.equals(DataSourceType.values().toSet)) {
      throw new IllegalArgumentException("Missing data source types!")
    }
    if (dataSourcesPriority.size != DataSourceType.values().length) {
      throw new IllegalArgumentException("There are duplicate types in data sources priority list!")
    }

    if (!searchCriterionTypes.toSet.equals(SearchCriterionType.getPublicTypes().asScala.toSet)) {
      throw new IllegalArgumentException("Missing search criterion types!")
    }
    if (searchCriterionTypes.size != SearchCriterionType.getPublicTypes().size()) {
      throw new IllegalArgumentException("There are duplicate types in search criterion types list!")
    }

    searchSettingsRequest.searchResultsFilters.foreach((searchFiltersByDataSource: SearchFiltersByDataSource) => {
      val dataSource = searchFiltersByDataSource.dataSource
      searchFiltersByDataSource.filters.foreach { filterConfig: SearchFilterConfig =>
        val searchFilter = SearchResultsFilter.fromType(filterConfig.filterType)

        if (searchFilter.dataSource != dataSource) {
          throw new IllegalArgumentException(s"Filter type ${filterConfig.filterType} does not belong to data source $dataSource")
        }
      }
    })

    searchSettingsRepository.save(searchSettings)
  }

  def getSearchSettings(userEmail: String): SearchSettingsRequestResponse = Option(searchSettingsRepository.findByUserEmail(userEmail).orElse(null))
    .map(searchSettings => SearchSettingsRequestResponse.fromSearchSettings(searchSettings))
    .getOrElse(getDefaultSearchSettings())

  def getDefaultSearchSettings(): SearchSettingsRequestResponse = {
    val dataSourceSettings = getDefaultDataSourceSettings()
    val searchCriteriaSettings = getDefaultSearchCriteriaSettings()
    val rankingEmployeeSearchSettings = getDefaultEmployeeRankingSearchSettings()
    val searchResultsFilteringSettings = getDefaultSearchResultsFilteringSettings(m365HasFirstPriority = isM365HighestPriorityDataSource())
    SearchSettingsRequestResponse(
      dataSourceSettings = dataSourceSettings,
      searchCriteria = searchCriteriaSettings.searchCriteria,
      searchResultsFilters = searchResultsFilteringSettings.searchResultsFilters,
      useReceivedEmailsContent = searchCriteriaSettings.useReceivedEmailsContent,
      freshness = rankingEmployeeSearchSettings.freshness,
      freshnessEnabled = rankingEmployeeSearchSettings.freshnessEnabled,
      volume = rankingEmployeeSearchSettings.volume,
      volumeEnabled = rankingEmployeeSearchSettings.volumeEnabled,
      relevanceScore = rankingEmployeeSearchSettings.relevanceScore,
      relevanceScoreEnabled = rankingEmployeeSearchSettings.relevanceScoreEnabled,
      freshnessBeginDate = rankingEmployeeSearchSettings.freshnessBeginDate,
      freshnessBeginDateEnabled = rankingEmployeeSearchSettings.freshnessBeginDateEnabled,
      includedEmailDomains = rankingEmployeeSearchSettings.includedEmailDomains,
      includedEmailDomainsEnabled = rankingEmployeeSearchSettings.includedEmailDomainsEnabled,
      excludedEmailDomains = rankingEmployeeSearchSettings.excludedEmailDomains,
      excludedEmailDomainsEnabled = rankingEmployeeSearchSettings.excludedEmailDomainsEnabled)
  }

  def getDefaultDataSourceSettings(): DataSourceSettings = {
    val dataSourcesPriority = if (isM365HighestPriorityDataSource()) {
      List(DataSourceType.M365, DataSourceType.HRData)
    } else {
      List(DataSourceType.HRData, DataSourceType.M365)
    }
    DataSourceSettings(dataSourcesPriority, isHRDataMandatory = false)
  }

  def getDefaultSearchCriteriaSettings(): SearchCriteriaSettings = {
    val searchCriteriaTypes = SearchCriterionType.getPublicTypes().asScala
      .map(searchCriterionType => SearchCriterion(searchCriterionType, isActive = true))
      .toList
    SearchCriteriaSettings(
      searchCriteriaTypes,
      useReceivedEmailsContent = true
    )
  }

  def getDefaultSearchResultsFilteringSettings(m365HasFirstPriority: Boolean): SearchResultsFilteringSettings = {
    SearchResultsFilteringSettings(List(
      SearchFiltersByDataSource(DataSourceType.M365,
        SearchResultsFilter.filtersByDataSource(DataSourceType.M365)
          .map(filter => SearchFilterConfig(filter.filterType, isActive = m365HasFirstPriority))),
      SearchFiltersByDataSource(DataSourceType.HRData,
        SearchResultsFilter.filtersByDataSource(DataSourceType.HRData)
          .map(filter => SearchFilterConfig(filter.filterType, isActive = !m365HasFirstPriority)))
    ))
  }

  def getDefaultEmployeeRankingSearchSettings(): EmployeeRankingSearchSettings = {
    EmployeeRankingSearchSettings(
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
      excludedEmailDomainsEnabled = true
    )
  }

  def getAzureSearchEmployeesIndexConfiguration(): Option[AzureSearchEmployeesIndexConfiguration] = {
    val entityManager: EntityManager = emf.createEntityManager()
    val azureSearchConfiguration = Option(entityManager.find(classOf[Configuration], ConfigurationTypes.AzureSearchEmployeesIndex)).map { config =>
      Configuration.toAzureSearchEmployeesIndexConfiguration(config)
    }
    entityManager.close()
    azureSearchConfiguration
  }

  def getAzureSearchEmployeeIndexName(): String = {
    val azsConfig = getAzureSearchEmployeesIndexConfiguration()

    val indexName = azsConfig match {
      case Some(azsConfig) => azsConfig.employeesIndexName
      case None => throw new AzureSearchIndexConfigurationMissing("employee")
    }

    indexName
  }

  def getLatestEmployeeProfileVersion(): LocalDateTime = {
    configurationRepository.findById(ConfigurationTypes.LatestVersionOfEmployeeProfile)
      .asScala
      .map { config =>
        Configuration.toLatestVersionOfEmployeeProfile(config).date
      }.getOrElse(TimeUtils.oldestSqlDate)
  }

  def getLatestHRDataEmployeeProfileVersion(): LocalDateTime = {
    configurationRepository.findById(ConfigurationTypes.LatestVersionOfHRDataEmployeeProfile)
      .asScala
      .map { config =>
        Configuration.toLatestVersionOfHRDataEmployeeProfile(config).date
      }.getOrElse(TimeUtils.oldestSqlDate)
  }

  def getLatestEmployeeInferredRolesVersion(): LocalDateTime = {
    configurationRepository.findById(ConfigurationTypes.LatestVersionOfInferredRoles)
      .asScala
      .map { config =>
        Configuration.toLatestVersionOfEmployeeInferredRoles(config).date
      }.getOrElse(TimeUtils.oldestSqlDate)
  }

  def isHRDataMandatory(userEmail: String): Boolean = {
    getSearchSettings(userEmail).dataSourceSettings.isHRDataMandatory
  }

  //The Cacheable method has to be called from an "outside" entity in order for caching to work
  @Cacheable(value = Array(CachingConfig.CACHE_NAME), condition = "#refreshIndexConfigCache==false", key = "#root.methodName")
  @CachePut(value = Array(CachingConfig.CACHE_NAME), condition = "#refreshIndexConfigCache==true", key = "#root.methodName")
  def getAzureSearchEmployeeIndexNameFromCache(refreshIndexConfigCache: Boolean): String = {
    getAzureSearchEmployeeIndexName()
  }

  //The Cacheable method has to be called from an "outside" entity in order for caching to work
  @Cacheable(value = Array(CachingConfig.CACHE_NAME), condition = "#refreshVersionConfigCache==false", key = "#root.methodName")
  @CachePut(value = Array(CachingConfig.CACHE_NAME), condition = "#refreshVersionConfigCache==true", key = "#root.methodName")
  def getLatestEmployeeProfileVersionFromCache(refreshVersionConfigCache: Boolean = false): LocalDateTime = {
    getLatestEmployeeProfileVersion()
  }

  //The Cacheable method has to be called from an "outside" entity in order for caching to work
  @Cacheable(value = Array(CachingConfig.CACHE_NAME), condition = "#refreshVersionConfigCache==false", key = "#root.methodName")
  @CachePut(value = Array(CachingConfig.CACHE_NAME), condition = "#refreshVersionConfigCache==true", key = "#root.methodName")
  def getLatestHRDataEmployeeProfileVersionFromCache(refreshVersionConfigCache: Boolean = false): LocalDateTime = {
    getLatestHRDataEmployeeProfileVersion()
  }

  //The Cacheable method has to be called from an "outside" entity in order for caching to work
  @Cacheable(value = Array(CachingConfig.CACHE_NAME), condition = "#refreshVersionConfigCache==false", key = "#root.methodName")
  @CachePut(value = Array(CachingConfig.CACHE_NAME), condition = "#refreshVersionConfigCache==true", key = "#root.methodName")
  def getLatestEmployeeInferredRolesVersionFromCache(refreshVersionConfigCache: Boolean = false): LocalDateTime = {
    getLatestEmployeeInferredRolesVersion()
  }

}
