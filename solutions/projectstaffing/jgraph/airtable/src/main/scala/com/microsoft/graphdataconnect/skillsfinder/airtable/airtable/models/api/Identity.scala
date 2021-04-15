package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api

import com.google.gson.annotations.SerializedName

trait Identity {

  @SerializedName("id")
  var id: String = _

}
