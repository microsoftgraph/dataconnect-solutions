package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import com.microsoft.graphdataconnect.model.configs.ConfigurationTypes
import com.microsoft.graphdataconnect.skillsfinder.db.entities.Configuration
import com.microsoft.graphdataconnect.skillsfinder.setup.repository.AbstractIntegrationTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class ConfigurationRepositoryJpaIntegrationTests extends AbstractIntegrationTestBase {

  private val logger = LoggerFactory.getLogger(classOf[ConfigurationRepositoryJpaIntegrationTests])

  @Autowired
  var configurationRepository: ConfigurationRepository = _

  @Test
  def repositoryWork(): Unit = {
    // Arrange
    val config = new Configuration()
    config.`type` = ConfigurationTypes.AzureBlobStorage
    config.configs = Map("key" -> "value")

    // Act
    val storedConfig = configurationRepository.save(config)

    // Assert
    assertThat(config.`type`.equals(storedConfig.`type`))
    assertThat(config.configs.equals(storedConfig.configs))
  }

}
