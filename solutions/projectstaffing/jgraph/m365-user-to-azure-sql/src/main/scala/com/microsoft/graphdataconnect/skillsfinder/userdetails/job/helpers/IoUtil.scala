package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.helpers

import java.util.Base64

object IoUtil {

  def encodePhoto(photoBytes: Option[Array[Byte]]): String = {
    photoBytes.map(bytes => Base64.getEncoder.encodeToString(bytes)).orNull
  }

}
