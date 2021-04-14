package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models

import com.google.gson.annotations.SerializedName
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api.Identity

class AirtableCurrentEngagement extends Identity {

  @SerializedName("Engagement Name")
  var name: String = _

}
