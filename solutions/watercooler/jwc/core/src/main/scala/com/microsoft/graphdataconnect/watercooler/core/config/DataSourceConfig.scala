/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.config

import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class DataSourceConfig {

  @Value("${spring.datasource.url:none}")
  var jdbcUrl: String = _

  @Value("${spring.datasource.driverClassName:none}")
  var dataSourceDriverClassName: String = _

  @Value("${jdbc.use.msi.sql.auth:false}")
  var useMsiSqlAuth: Boolean = _

  @Value("${spring.datasource.username}")
  var azureSqlUser: String = _

  @Value("${spring.datasource.password}")
  var azureSqlPassword: String = _

  @Bean
  def getDataSource: DataSource = {
    val dataSourceBuilder: DataSourceBuilder[_] = DataSourceBuilder.create()
    if (!useMsiSqlAuth) {
      dataSourceBuilder.username(azureSqlUser)
      dataSourceBuilder.password(azureSqlPassword)
    }
    dataSourceBuilder.driverClassName(dataSourceDriverClassName)
    dataSourceBuilder.url(jdbcUrl)
    dataSourceBuilder.build().asInstanceOf[DataSource]
  }

}
