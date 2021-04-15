package com.microsoft.graphdataconnect.skillsfinder.exceptions

class AzureSearchIndexConfigurationMissing(indexConfigurationType: String) extends RuntimeException(s"Missing Azure Search $indexConfigurationType index configuration") {

}
