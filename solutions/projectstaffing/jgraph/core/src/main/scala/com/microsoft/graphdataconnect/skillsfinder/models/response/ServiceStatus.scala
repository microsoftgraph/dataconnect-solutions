package com.microsoft.graphdataconnect.skillsfinder.models.response

import org.springframework.http.HttpStatus

case class ServiceStatus(name: String, status: HttpStatus, message: String, isError: Boolean) {

}
