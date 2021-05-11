/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.config

import java.io.IOException

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.{ClassPathResource, Resource}
import org.springframework.web.servlet.config.annotation.{ResourceHandlerRegistry, WebMvcConfigurer}
import org.springframework.web.servlet.resource.PathResourceResolver

@Configuration
class MvcConfig extends WebMvcConfigurer {

  override def addResourceHandlers(registry: ResourceHandlerRegistry): Unit = {
    registry.addResourceHandler("/**/*").addResourceLocations("classpath:/public/").resourceChain(true).addResolver(new PathResourceResolver() {
      @throws[IOException]
      override protected def getResource(resourcePath: String, location: Resource): Resource = {
        val requestedResource = location.createRelative(resourcePath)
        //        Ignore App Service's AD autentication routes
        if (resourcePath.startsWith(".auth/")) {
          requestedResource
        } else {
          if (requestedResource.exists && requestedResource.isReadable) requestedResource else new ClassPathResource("/public/index.html")
        }
      }
    })
  }
}
