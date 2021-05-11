/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api

import com.google.gson.annotations.SerializedName

class AirtableRecords[T] {

  @SerializedName("records")
  var records: Array[AirtableRecord[T]] = _

  @SerializedName("offset")
  var offset: String = _

}
