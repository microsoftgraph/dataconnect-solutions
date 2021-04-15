package com.microsoft.graphdataconnect.skillsfinder.utils

import com.microsoft.graphdataconnect.utils.TimeUtils
import org.junit.{Assert, Test}

class TimeUtilsTests {

  @Test
  def isBeforeNotAbsolute(): Unit = {
    Assert.assertTrue("2020-01-20 is before 2020-01-30",
      TimeUtils.isBeforeOrEqual("2020-01-20", "2020-01-30"))

    Assert.assertTrue("2020-01-20 is before 2020-01-21",
      TimeUtils.isBeforeOrEqual("2020-01-20", "2020-01-21"))

    Assert.assertTrue("2020-01-20 should be be before the same day",
      TimeUtils.isBeforeOrEqual("2020-01-20", "2020-01-20"))

    Assert.assertFalse("2020-01-30 is after 2020-01-20",
      TimeUtils.isBeforeOrEqual("2020-01-30", "2020-01-20"))

    Assert.assertFalse("2020-01-21 is after 2020-01-20",
      TimeUtils.isBeforeOrEqual("2020-01-21", "2020-01-20"))

  }

}
