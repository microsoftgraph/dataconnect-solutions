/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.setup.repository

import com.microsoft.graphdataconnect.skillsfinder.config.DataConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.{Configuration, Import}


@Configuration
@EnableAutoConfiguration
@Import(Array(classOf[DataConfiguration]))
class BaseTestDataConfiguration {

}
