package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.helpers

import java.time.Instant

trait TimeHelper {

  def nowUTC(): Long = {
    Instant.now().getEpochSecond
  }

}
