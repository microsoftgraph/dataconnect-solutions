package com.microsoft.graphdataconnect.skillsfinder.config

import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class DataSourceConfig {

  @Value("${jdbc.url}")
  var jdbcUrl: String = _

  @Value("${spring.datasource.driverClassName}")
  var dataSourceDriverClassName: String = _

  @Value("${jdbc.use.msi.sql.auth}")
  var useMsiSqlAuth: Boolean = _

  @Value("${azure-sql-user:sa}")
  var azureSqlUser: String = _

  @Value("${azure-sql-password:password_!23}")
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
