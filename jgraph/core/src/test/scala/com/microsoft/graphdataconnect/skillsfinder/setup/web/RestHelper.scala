package com.microsoft.graphdataconnect.skillsfinder.setup.web

import java.net.URI

import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.skillsfinder.setup.Constants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.{HttpHeaders, MediaType}
import org.springframework.util.Assert

class RestHelper {

  @Autowired
  var restClient: TestRestTemplate = _
  @Autowired
  var objectMapper: ObjectMapper = _

  def getBaseHeaders: HttpHeaders = {
    val headers = new HttpHeaders
    headers.setContentType(MediaType.APPLICATION_JSON)
    headers.set("x-ms-client-principal-name", Constants.CLIENT_EMAIL)
    headers
  }

  protected def getRelativeResourceURI(resource: String): String = {
    Assert.notNull(resource, "Resource is required!")
    URI.create(resource).toString
  }

  protected def getRelativeResourceURI(resource: String, id: Long): String = {
    Assert.notNull(resource, "Resource is required!")
    URI.create(s"$resource/$id").toString
  }

}
