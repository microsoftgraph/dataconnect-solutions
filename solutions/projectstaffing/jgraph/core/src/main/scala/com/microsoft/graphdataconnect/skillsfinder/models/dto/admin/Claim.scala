package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.fasterxml.jackson.annotation.JsonProperty

case class Claim(typ: String, @JsonProperty("val") value: String)
