package com.microsoft.graphdataconnect.skillsfinder.exceptions

class AzureSearchIndexNotFound(indexName: String) extends Exception(s"Azure Search index with name '$indexName' does not exist") {

}
