/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.meta

import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.meta.ApplicationInfo
import com.microsoft.graphdataconnect.watercooler.core.controllers.responses.meta.{ApplicationInfo, ServiceStatus}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.{GetMapping, RequestHeader, RequestMapping, RestController}


@RestController
@RequestMapping(Array("/jwc"))
class JwcInfoController(@Autowired jdbcTemplate: JdbcTemplate,
                        @Autowired buildProperties: BuildProperties) {

  private val log: Logger = LoggerFactory.getLogger(classOf[JwcInfoController])

  @GetMapping(Array("/status"))
  def healthCheck(@RequestHeader httpHeaders: HttpHeaders): ResponseEntity[List[ServiceStatus]] = {
    log.info(s"GET [status]")

    val jwcStatus = ServiceStatus(JwcInfoController.APP_NAME, HttpStatus.OK, "App is running", isError = false);

    //    val accessToken = httpHeaders.toSingleValueMap.asScala.get("x-ms-token-aad-access-token")
    val sqlStatus = sqlHealthCheck()

    val statusList: List[ServiceStatus] = List(jwcStatus, sqlStatus)
    ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(statusList)
  }

  @GetMapping(Array("/about"))
  def about(): ResponseEntity[ApplicationInfo] = {
    val appInfo = ApplicationInfo(
      JwcInfoController.APP_NAME,
      buildProperties.getVersion,
      buildProperties.getTime.toString,
      buildProperties.get("scm.branch"),
      buildProperties.get("scm.commit"),
      buildProperties.get("java.version"),
      buildProperties.get("scala.version"),
      buildProperties.get("docker.tag")
    )
    log.info("Current application info " + appInfo)

    ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(appInfo)
  }

  private def sqlHealthCheck(): ServiceStatus = {
    log.info(s"GET [statussql]")
    try {
      val res = jdbcTemplate.queryForObject("SELECT 1", classOf[Integer])

      if (res == 1)
        ServiceStatus("sqlserver", HttpStatus.OK, "App is running", isError = false)
      else
        ServiceStatus("sqlserver", HttpStatus.EXPECTATION_FAILED, "Error running query", isError = true)
    } catch {
      case e: Exception =>
        log.warn("sql status query failed " + e.getMessage)
        ServiceStatus("sqlserver", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage, isError = true)
    }
  }

}

object JwcInfoController {
  val APP_NAME = "WaterCooler"
}
