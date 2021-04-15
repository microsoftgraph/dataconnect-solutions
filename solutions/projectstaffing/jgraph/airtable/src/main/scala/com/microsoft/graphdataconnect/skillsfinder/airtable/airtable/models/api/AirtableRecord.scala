package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api

import com.google.gson.annotations.SerializedName

class AirtableRecord[T] extends Identity {

  @SerializedName("fields")
  var fields: T = _

}
