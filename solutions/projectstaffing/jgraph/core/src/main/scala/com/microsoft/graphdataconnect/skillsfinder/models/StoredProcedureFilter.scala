package com.microsoft.graphdataconnect.skillsfinder.models

case class StoredProcedureFilter(dbTable: String, dbColumn: String, filterValues: List[String])
