package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.fasterxml.jackson.annotation.JsonProperty

case class UserInfo(@JsonProperty("access_token") accessToken: String,
                    @JsonProperty("id_token") idToken: String,
                    @JsonProperty("refresh_token") refreshToken: String,
                    @JsonProperty("user_id") userId: String)
