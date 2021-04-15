package com.microsoft.graphdataconnect.skillsfinder.models.business

case class Opportunity(id: Long,
                       name: String,
                       owner: String,
                       value: Long,
                       details: String)
