package com.microsoft.graphdataconnect.skillsfinder.exceptions

case class PermissionConsentMissingException(message: String, exception: Throwable = null, consentRequestUrl: String) extends Exception(message, exception) {

}
