package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api

import com.google.gson.annotations.SerializedName

class AirtableRecords[T] {

  @SerializedName("records")
  var records: Array[AirtableRecord[T]] = _

  @SerializedName("offset")
  var offset: String = _

}
