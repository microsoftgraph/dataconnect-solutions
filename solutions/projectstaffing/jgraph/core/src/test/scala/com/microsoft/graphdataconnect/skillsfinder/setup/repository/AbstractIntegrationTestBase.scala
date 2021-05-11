/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.setup.repository

import com.microsoft.graphdataconnect.skillsfinder.setup.InstanceClassTestBase
import org.junit.Ignore
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED
import org.springframework.test.context.jdbc.{Sql, SqlConfig}
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional


@Ignore
@RunWith(classOf[SpringRunner])
@SpringBootTest(properties = Array("spring.profiles.active=test"))
@ContextConfiguration(classes = Array(classOf[BaseTestDataConfiguration]))
@Transactional
@Sql(scripts = Array("/test/db/migration/custom.sql"), config = new SqlConfig(transactionMode = ISOLATED))
class AbstractIntegrationTestBase extends InstanceClassTestBase {

  override def beforeClassSetup(): Unit = {
    // nothing to do
  }

  override def afterClassSetup(): Unit = {
    // nothing to do
  }

}
