/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models

import com.google.gson.annotations.SerializedName
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api.Identity

class AirtableCurrentEngagement extends Identity {

  @SerializedName("Engagement Name")
  var name: String = _

}
