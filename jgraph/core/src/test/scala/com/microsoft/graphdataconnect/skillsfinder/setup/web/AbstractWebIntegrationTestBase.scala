package com.microsoft.graphdataconnect.skillsfinder.setup.web

import com.microsoft.graphdataconnect.skillsfinder.Runner
import com.microsoft.graphdataconnect.skillsfinder.setup.InstanceClassTestBase
import org.junit.Ignore
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest

@Ignore
@RunWith(classOf[SpringInstanceTestClassRunner])
@SpringBootTest(
  properties = Array("spring.profiles.active=test"),
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = Array(classOf[Runner]))
class AbstractWebIntegrationTestBase extends InstanceClassTestBase {

  override def beforeClassSetup(): Unit = {
    // nothing to do
  }

  override def afterClassSetup(): Unit = {
    // nothing to do
  }

}
