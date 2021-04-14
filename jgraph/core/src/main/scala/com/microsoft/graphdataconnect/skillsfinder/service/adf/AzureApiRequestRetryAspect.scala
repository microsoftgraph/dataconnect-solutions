package com.microsoft.graphdataconnect.skillsfinder.service.adf

import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.UserToken
import com.microsoft.graphdataconnect.skillsfinder.service.UserService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Aspect
@Component
class AzureApiRequestRetryAspect {

  private val logger: Logger = LoggerFactory.getLogger(classOf[AzureApiRequestRetryAspect])

  @Autowired
  var userService: UserService = _

  val numbOfRetryAttempts = 5

  @Around("@annotation(AzureApiRequestRetry)")
  def retryAdfRequest(joinPoint: ProceedingJoinPoint): Any = {
    try {
      joinPoint.proceed()
    } catch {
      case e: ResponseStatusException =>
        if (e.getStatus.equals(org.springframework.http.HttpStatus.NOT_FOUND) && e.getMessage.contains("There is no pipeline run with id")) {
          //It might be that the pipeline run is not yet created because of a delay from ADF,
          // between the time the trigger was started and the pipeline run is created
          logger.info("Retrying get pipeline run status request.")
          Thread.sleep(20000)
          joinPoint.proceed()
        } else if (e.getStatus.equals(org.springframework.http.HttpStatus.UNAUTHORIZED) || e.getStatus.equals(org.springframework.http.HttpStatus.FORBIDDEN)) {
          retryRequestUsingRefreshedToken(joinPoint)
        } else {
          logger.error("Azure API request failed", e)
          throw e
        }
      case e: Exception =>
        logger.error("Azure API request failed", e)
        retryRequestUsingRefreshedToken(joinPoint)
    }
  }

  private def retryRequestUsingRefreshedToken(joinPoint: ProceedingJoinPoint): Any = {
    logger.info("Refreshing user access token")
    val args: Array[AnyRef] = joinPoint.getArgs
    val userTokenOptAnyRef = args.find(_.isInstanceOf[UserToken])
    if (userTokenOptAnyRef.isDefined) {
      val userToken = userTokenOptAnyRef.get.asInstanceOf[UserToken]
      val newUserToken = userService.getUserToken(refreshToken = userToken.refreshToken, scope = userToken.tokenScope)
      userToken.refreshToken = newUserToken.refreshToken
      userToken.accessToken = newUserToken.accessToken
    }

    joinPoint.proceed()
  }

}
