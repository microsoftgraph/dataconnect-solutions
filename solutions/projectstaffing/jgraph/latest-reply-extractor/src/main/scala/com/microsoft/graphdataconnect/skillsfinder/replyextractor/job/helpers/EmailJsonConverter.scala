/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.helpers

import org.apache.spark.rdd.RDD
import spray.json._

trait EmailJsonConverter {

  def convertEmailsAsJson(emailsRaw: RDD[String]): RDD[JsObject] = {
    emailsRaw.map(x => {
      val emailJsonValue: JsValue = x.parseJson
      emailJsonValue.asJsObject
    })
  }

}

