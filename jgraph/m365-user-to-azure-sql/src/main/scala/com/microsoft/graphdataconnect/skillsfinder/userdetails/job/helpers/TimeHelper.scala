package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.helpers

import java.time.Instant

trait TimeHelper {

  def nowUTC(): Long = {
    Instant.now().getEpochSecond
  }

}
