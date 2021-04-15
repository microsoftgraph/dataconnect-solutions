package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.skillsfinder.models.response.{ApplicationInfo, ServiceStatus}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.{GetMapping, RequestHeader, RequestMapping, RestController}


@RestController
@RequestMapping(Array("/gdc"))
class GdcController(@Autowired jdbcTemplate: JdbcTemplate) {

  @Autowired var buildProperties: BuildProperties = _

  private val log: Logger = LoggerFactory.getLogger(classOf[GdcController])

  @GetMapping(Array("/status"))
  def healthCheck(@RequestHeader httpHeaders: HttpHeaders): ResponseEntity[List[ServiceStatus]] = {
    log.info(s"GET [status]")

    val jgraphStatus = ServiceStatus("jgraph", HttpStatus.OK, "App is running", isError = false);

    //    val accessToken = httpHeaders.toSingleValueMap.asScala.get("x-ms-token-aad-access-token")
    val sqlStatus = sqlHealthCheck()

    val statusList: List[ServiceStatus] = List(jgraphStatus, sqlStatus)
    ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(statusList)
  }

  @GetMapping(Array("/about"))
  def about(): ResponseEntity[ApplicationInfo] = {
    val appInfo = ApplicationInfo(
      GdcController.APP_NAME,
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

object GdcController {
  val APP_NAME = "GDC"
}
