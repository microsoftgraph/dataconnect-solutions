package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.skillsfinder.models.configs.DatabricksConfiguration
import com.microsoft.graphdataconnect.skillsfinder.setup.web.AbstractWebIntegrationTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ConfigurationControllerWebIntegrationTests extends AbstractWebIntegrationTestBase {

  @Autowired
  var gdcRestClient: GDCRestClient = _

  private val log: Logger = LoggerFactory.getLogger(classOf[ConfigurationControllerWebIntegrationTests])

  @Test
  @Transactional
  def testGetDatabricksConfiguration_WithoutSecret(): Unit = {
    // Arrange
    val storedDatabricksConfiguration = DatabricksConfiguration("https://addressVal0", "apiTokenVal0", "orgIdVal0", "clusterIdVal0", "portVal0")
    gdcRestClient.createDatabricksConfiguration(storedDatabricksConfiguration)

    // Act
    val responseDatabricksConfiguration: DatabricksConfiguration = gdcRestClient.getDatabricksConfiguration()

    // Assert
    assertThat(responseDatabricksConfiguration.apiToken).isEmpty()
    assertThat(storedDatabricksConfiguration.address).isEqualTo(responseDatabricksConfiguration.address)
    assertThat(storedDatabricksConfiguration.orgId).isEqualTo(responseDatabricksConfiguration.orgId)
    assertThat(storedDatabricksConfiguration.clusterId).isEqualTo(responseDatabricksConfiguration.clusterId)
    assertThat(storedDatabricksConfiguration.port).isEqualTo(responseDatabricksConfiguration.port)
  }

  @Test
  @Transactional
  def testGetDatabricksConfiguration_WithSecret(): Unit = {
    // Arrange
    val storedDatabricksConfiguration = DatabricksConfiguration("https://addressVal1", "apiTokenVal1", "orgIdVal1", "clusterIdVal1", "portVal1")
    gdcRestClient.createDatabricksConfiguration(storedDatabricksConfiguration)

    // Act
    val responseDatabricksConfiguration: DatabricksConfiguration = gdcRestClient.getDatabricksConfiguration(Some(true))

    // Assert
    assertThat(storedDatabricksConfiguration.apiToken).isEqualTo(responseDatabricksConfiguration.apiToken)
    assertThat(storedDatabricksConfiguration.address).isEqualTo(responseDatabricksConfiguration.address)
    assertThat(storedDatabricksConfiguration.orgId).isEqualTo(responseDatabricksConfiguration.orgId)
    assertThat(storedDatabricksConfiguration.clusterId).isEqualTo(responseDatabricksConfiguration.clusterId)
    assertThat(storedDatabricksConfiguration.port).isEqualTo(responseDatabricksConfiguration.port)
  }

  @Test
  @Transactional
  def testCreateOrUpdateDatabricksConfiguration(): Unit = {
    // Arrange
    val requestDatabricksConfiguration = DatabricksConfiguration("https://addressVal2", "apiTokenVal2", "orgIdVal2", "clusterIdVal2", "portVal2")

    // Act
    val responseDatabricksConfiguration: DatabricksConfiguration = gdcRestClient.createDatabricksConfiguration(requestDatabricksConfiguration)

    // Assert
    assertThat(requestDatabricksConfiguration.address).isEqualTo(responseDatabricksConfiguration.address)
    assertThat(requestDatabricksConfiguration.apiToken).isEqualTo(responseDatabricksConfiguration.apiToken)
    assertThat(requestDatabricksConfiguration.orgId).isEqualTo(responseDatabricksConfiguration.orgId)
    assertThat(requestDatabricksConfiguration.clusterId).isEqualTo(responseDatabricksConfiguration.clusterId)
    assertThat(requestDatabricksConfiguration.port).isEqualTo(responseDatabricksConfiguration.port)
  }

}
