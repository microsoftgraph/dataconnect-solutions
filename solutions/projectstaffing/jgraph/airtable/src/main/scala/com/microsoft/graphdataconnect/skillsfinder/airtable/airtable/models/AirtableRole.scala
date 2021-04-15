package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models

import com.google.gson.annotations.SerializedName
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api.Identity

class AirtableRole extends Identity {

  @SerializedName("Name")
  var name: String = _
}
