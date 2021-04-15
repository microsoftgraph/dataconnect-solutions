package com.microsoft.graphdataconnect.skillsfinder.service

import java.time.LocalDateTime

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduleService {
  private val log: Logger = LoggerFactory.getLogger(classOf[ScheduleService])

  @Autowired
  var configurationService: ConfigurationService = _

  @Scheduled(fixedRateString = "${scheduler.delay.employee-version}", initialDelayString = "${scheduler.delay.initial}")
  def checkEmployeeProfileVersionConfiguration(): Unit = {
    log.trace("Checking for latest version of Employee profile data")
    val oldVersion = configurationService.getLatestEmployeeProfileVersionFromCache()
    val latestVersion: LocalDateTime = configurationService.getLatestEmployeeProfileVersionFromCache(refreshVersionConfigCache = true)
    if (!oldVersion.isEqual(latestVersion)) {
      log.info(s"Latest version of Employee profile data is: {$latestVersion}")
    }
  }

  @Scheduled(fixedRateString = "${scheduler.delay.employee-version}", initialDelayString = "${scheduler.delay.initial}")
  def checkHRDataEmployeeProfileVersionConfiguration(): Unit = {
    log.trace("Checking for latest version of HR Data Employee profile")
    val oldVersion = configurationService.getLatestHRDataEmployeeProfileVersionFromCache()
    val latestVersion: LocalDateTime = configurationService.getLatestHRDataEmployeeProfileVersionFromCache(refreshVersionConfigCache = true)
    if (!oldVersion.isEqual(latestVersion)) {
      log.info(s"Latest version of HR Data Employee profile is: {$latestVersion}")
    }
  }

  @Scheduled(fixedRateString = "${scheduler.delay.employee-version}", initialDelayString = "${scheduler.delay.initial}")
  def checkEmployeeInferredRolesVersionConfiguration(): Unit = {
    log.trace("Checking for latest version of Employee inferred roles data")
    val oldVersion = configurationService.getLatestEmployeeInferredRolesVersionFromCache()
    val latestVersion: LocalDateTime = configurationService.getLatestEmployeeInferredRolesVersionFromCache(refreshVersionConfigCache = true)
    if (!oldVersion.isEqual(latestVersion)) {
      log.info(s"Latest version of Employee inferred roles data is: {$latestVersion}")
    }
  }

}
