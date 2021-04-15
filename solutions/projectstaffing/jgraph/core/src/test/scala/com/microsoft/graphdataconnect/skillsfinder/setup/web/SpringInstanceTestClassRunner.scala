package com.microsoft.graphdataconnect.skillsfinder.setup.web

import com.microsoft.graphdataconnect.skillsfinder.setup.InstanceClassTestBase
import org.junit.runner.notification.RunNotifier
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner


class SpringInstanceTestClassRunner(clazz: Class[_]) extends SpringJUnit4ClassRunner(clazz) {
  var instanceSetupListener: InstanceClassTestBase = _

  @throws[Exception]
  override protected def createTest: AnyRef = {
    val test = super.createTest
    // Note that JUnit4 will call this createTest() multiple times for each
    // test method, so we need to ensure to call "beforeClassSetup" only once.
    test match {
      case listener: InstanceClassTestBase if instanceSetupListener == null =>
        instanceSetupListener = listener
        instanceSetupListener.beforeClassSetup()
      case _ =>
    }
    test
  }

  override def run(notifier: RunNotifier): Unit = {
    super.run(notifier)
    if (instanceSetupListener != null) instanceSetupListener.afterClassSetup()
  }
}
