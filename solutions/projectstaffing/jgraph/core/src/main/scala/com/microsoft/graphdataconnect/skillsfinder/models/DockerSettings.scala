package com.microsoft.graphdataconnect.skillsfinder.models

import org.apache.commons.lang3.StringUtils

case class DockerSettings(dockerUser: String, dockerPassword: String, executorImageUrl: String) {

  require(StringUtils.isNotBlank(executorImageUrl), "image url is empty")
}
