package com.microsoft.graphdataconnect.skillsfinder.models.business.team

case class TeamDownloadResponse(
                                 ownerMail: String,
                                 name: String,
                                 description: String,
                                 members: Seq[TeamDownloadMemberResponse]
                               )
