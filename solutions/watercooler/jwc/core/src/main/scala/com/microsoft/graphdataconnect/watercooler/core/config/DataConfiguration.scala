/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.{ComponentScan, Configuration}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@Configuration
@EnableJpaRepositories(basePackages = Array("com.microsoft.graphdataconnect.watercooler.common.db.repositories"))
@EntityScan(basePackages = Array("com.microsoft.graphdataconnect.watercooler.common.db.entities"))
@ComponentScan(basePackages = Array("com.microsoft.graphdataconnect.watercooler.common.services"))
class DataConfiguration {

}
