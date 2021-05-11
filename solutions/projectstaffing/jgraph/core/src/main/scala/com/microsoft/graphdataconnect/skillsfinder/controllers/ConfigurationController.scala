/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.controllers


import java.util.Optional

import com.microsoft.graphdataconnect.model.configs.AzureSearchEmployeesIndexConfiguration
import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.DataSourceType
import com.microsoft.graphdataconnect.skillsfinder.exceptions.ResourceNotFoundException
import com.microsoft.graphdataconnect.skillsfinder.models._
import com.microsoft.graphdataconnect.skillsfinder.models.configs.{AzureBlobStorageCredential, DatabricksConfiguration, HDInsightConfiguration, OAuthAzureActiveDirectoryConfiguration}
import com.microsoft.graphdataconnect.skillsfinder.models.response.ResponseMessage
import com.microsoft.graphdataconnect.skillsfinder.service.{ConfigurationService, UserService}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation._


@Controller
@RequestMapping(Array("/gdc/configurations"))
class ConfigurationController(@Autowired configurationService: ConfigurationService,
                              @Autowired userService: UserService) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[ConfigurationController])

  @GetMapping(Array("/hdinsight"))
  def getHDInsightConfiguration(@RequestParam(required = false, defaultValue = "false") includeSecret: Boolean = false): ResponseEntity[HDInsightConfiguration] = {
    configurationService.getHDInsightConfiguration(includeSecret) match {
      case Some(hdInsightConfiguration) =>
        ResponseEntity.status(HttpStatus.OK).body(hdInsightConfiguration)
      case None =>
        throw new ResourceNotFoundException("HDInsight configuration not found, please set one up.")
    }
  }

  @PostMapping(Array("/hdinsight"))
  @Transactional
  def createOrUpdateHDInsightConfiguration(@RequestBody createHDInsightConfig: HDInsightConfiguration): ResponseEntity[ResponseMessage] = {
    configurationService.createOrUpdateHDInsightConfiguration(createHDInsightConfig)
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("HDInsight configuration saved."))
  }

  @DeleteMapping(Array("/hdinsight"))
  @Transactional
  def deleteHDInsightConfiguration(): ResponseEntity[ResponseMessage] = {
    configurationService.deleteHDInsightConfiguration()
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("HDInsight configuration deleted."))
  }


  @GetMapping(Array("/databricks"))
  def getDatabricksConfiguration(@RequestParam(required = false, defaultValue = "false") includeSecret: Boolean = false): ResponseEntity[DatabricksConfiguration] = {
    configurationService.getDatabricksConfiguration(includeSecret) match {
      case Some(databricksConfig) =>
        ResponseEntity.status(HttpStatus.OK).body(databricksConfig)
      case None =>
        throw new ResourceNotFoundException("Databricks configuration not found, please set one up.")
    }
  }

  @PostMapping(Array("/databricks"))
  @Transactional
  def createOrUpdateDatabricksConfiguration(@RequestBody createDatabricksConfig: DatabricksConfiguration): ResponseEntity[DatabricksConfiguration] = {
    val config: DatabricksConfiguration = configurationService.createOrUpdateDatabricksConfiguration(createDatabricksConfig)
    ResponseEntity.status(HttpStatus.OK).body(config)
  }

  @DeleteMapping(Array("/databricks"))
  @Transactional
  def deleteDatabricksConfiguration(): ResponseEntity[ResponseMessage] = {
    configurationService.deleteDatabricksConfiguration()
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Databricks configuration deleted."))
  }


  @ResponseBody
  @GetMapping(Array("/azure-storage"))
  def getBlobStorageAccountDetails(@RequestParam(required = false, defaultValue = "false") includeSecret: Boolean = false): ResponseEntity[AzureBlobStorageCredential] = {
    configurationService.getBlobStorageAccountDetails(includeSecret) match {
      case Some(azureBlobStorageConfig) =>
        ResponseEntity.status(HttpStatus.OK).body(azureBlobStorageConfig)
      case None =>
        throw new ResourceNotFoundException("Azure storage configuration not be found, please set one up.")
    }
  }

  @ResponseBody
  @PostMapping(Array("/azure-storage"))
  @Transactional
  def createOrUpdateBlobStorageDetails(@RequestBody blobStorageDetails: AzureBlobStorageCredential): ResponseEntity[ResponseMessage] = {
    configurationService.createOrUpdateBlobStorageDetails(blobStorageDetails)
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Azure storage configuration updated."))
  }

  @DeleteMapping(Array("/azure-storage"))
  @Transactional
  def deleteBlobStorageDetails(): ResponseEntity[ResponseMessage] = {
    configurationService.deleteBlobStorageDetails()
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Azure storage configuration deleted."))
  }


  @GetMapping(Array("/azure-active-directory"))
  def getAzureActiveDirectoryConfiguration(): ResponseEntity[OAuthAzureActiveDirectoryConfiguration] = {
    configurationService.getAzureActiveDirectoryConfiguration() match {
      case Some(config) =>
        ResponseEntity.status(HttpStatus.OK).body(config)
      case None => throw new ResourceNotFoundException("Azure Active Directory configuration not found, please set one up.")
    }
  }

  @GetMapping(Array("/azure-active-directory-enabled"))
  def isAzureActiveDirectoryEnabled(): ResponseEntity[Any] = {
    configurationService.getAzureActiveDirectoryConfiguration() match {
      case Some(_) =>
        ResponseEntity.status(HttpStatus.OK).body(true)
      case None => ResponseEntity.status(HttpStatus.OK).body(false)
    }
  }

  @ResponseBody
  @PostMapping(Array("/azure-active-directory"))
  @Transactional
  def createOrUpdateAzureActiveDirectoryDetails(@RequestBody azureActiveDirectory: OAuthAzureActiveDirectoryConfiguration): ResponseEntity[ResponseMessage] = {
    configurationService.createOrUpdateAzureActiveDirectoryDetails(azureActiveDirectory)
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Azure Active Directory configuration updated."))
  }

  @DeleteMapping(Array("/azure-active-directory"))
  @Transactional
  def deleteAzureActiveDirectoryDetails(): ResponseEntity[ResponseMessage] = {
    configurationService.deleteAzureActiveDirectoryDetails()
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Azure Active Directory configuration deleted."))
  }

  @ResponseBody
  @PostMapping(Array("/search-settings"))
  @Transactional
  def createOrUpdateSearchSettings(@RequestBody searchSettingsRequest: SearchSettingsRequestResponse,
                                   @RequestAttribute("userId") userId: String): ResponseEntity[ResponseMessage] = {
    configurationService.saveSearchSettings(searchSettingsRequest, userId)
    ResponseEntity.status(HttpStatus.OK).body(ResponseMessage("Employee search settings updated."))
  }

  @GetMapping(Array("/search-settings"))
  def getSearchSettings(@RequestAttribute("userId") userId: String): ResponseEntity[SearchSettingsRequestResponse] = {
    val searchSettings = configurationService.getSearchSettings(userEmail = userId)
    ResponseEntity.status(HttpStatus.OK).body(searchSettings)
  }

  @GetMapping(Array("/search-settings/default-data-source-settings"))
  def getDefaultDataSourceSettings(): ResponseEntity[DataSourceSettings] = {
    val dataSourceSettings = configurationService.getDefaultDataSourceSettings()
    ResponseEntity.status(HttpStatus.OK).body(dataSourceSettings)
  }

  @GetMapping(Array("/search-settings/default-search-criteria"))
  def getDefaultSearchCriteria(): ResponseEntity[SearchCriteriaSettings] = {
    val searchCriteriaSettings = configurationService.getDefaultSearchCriteriaSettings()
    ResponseEntity.status(HttpStatus.OK).body(searchCriteriaSettings)
  }

  @GetMapping(Array("/search-settings/default-employee-ranking"))
  def getDefaultRankingEmployeeSearchSettings(): ResponseEntity[EmployeeRankingSearchSettings] = {
    val rankingEmployeeSearchSettings = configurationService.getDefaultEmployeeRankingSearchSettings()
    ResponseEntity.status(HttpStatus.OK).body(rankingEmployeeSearchSettings)
  }

  @GetMapping(Array("/search-settings/default-search-results-filtering-settings"))
  def getDefaultSearchResultsFilteringSettings(@RequestParam dataSourcesPriority: Optional[String],
                                               @RequestAttribute("userId") userId: String): ResponseEntity[SearchResultsFilteringSettings] = {
    val actualDataSourcesPriority = if (dataSourcesPriority != null && dataSourcesPriority.isPresent) {
      dataSourcesPriority.get.split(",").map(DataSourceType.valueOf(_)).toList
    } else {
      val searchSettings = configurationService.getSearchSettings(userId)
      searchSettings.dataSourceSettings.dataSourcesPriority
    }
    val m365HasFirstPriority = actualDataSourcesPriority.head.equals(DataSourceType.M365)
    val searchResultsFilteringSettings = configurationService.getDefaultSearchResultsFilteringSettings(m365HasFirstPriority)
    ResponseEntity.status(HttpStatus.OK).body(searchResultsFilteringSettings)
  }

  @GetMapping(Array("/azure-search/employees-index"))
  def getAzureSearchEmployeesIndexConfiguration(): ResponseEntity[AzureSearchEmployeesIndexConfiguration] = {
    configurationService.getAzureSearchEmployeesIndexConfiguration() match {
      case Some(azureSearchEmployeeIndexConfiguration) =>
        ResponseEntity.status(HttpStatus.OK).body(azureSearchEmployeeIndexConfiguration)
      case None =>
        throw new ResourceNotFoundException("Azure Search employees index configuration not found, please set one up.")
    }
  }

}
