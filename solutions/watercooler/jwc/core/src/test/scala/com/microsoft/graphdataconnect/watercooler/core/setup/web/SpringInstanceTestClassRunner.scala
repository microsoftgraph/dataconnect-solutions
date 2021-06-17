/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.setup.web

import com.microsoft.graphdataconnect.watercooler.core.setup.InstanceClassTestBase
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
