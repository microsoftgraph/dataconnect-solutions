package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.microsoft.graphdataconnect.skillsfinder.models.TokenScope

case class UserToken(var accessToken: String,
                     var refreshToken: String,
                     var tokenScope: TokenScope)
