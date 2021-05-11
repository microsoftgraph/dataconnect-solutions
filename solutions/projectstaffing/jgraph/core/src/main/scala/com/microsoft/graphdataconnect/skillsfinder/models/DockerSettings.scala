/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models

import org.apache.commons.lang3.StringUtils

case class DockerSettings(dockerUser: String, dockerPassword: String, executorImageUrl: String) {

  require(StringUtils.isNotBlank(executorImageUrl), "image url is empty")
}
