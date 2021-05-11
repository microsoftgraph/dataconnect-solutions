/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models

import com.google.gson.annotations.SerializedName
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api.Identity

/*  Known values
  0 = "Principal Consultant"
  1 = "N/A (not a consultant)"
  2 = "Principal Architect"
  3 = "Mid Level Consultant"
  4 = "Senior Consultant"
  5 = "Entry Level "
 */
class AirtableConsultancyType extends Identity {

  @SerializedName("Name")
  var name: String = _

}
