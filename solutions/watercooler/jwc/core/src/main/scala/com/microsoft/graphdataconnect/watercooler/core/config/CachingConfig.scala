/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.config

import java.util

import com.microsoft.graphdataconnect.watercooler.common.util.Constants
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.{ConcurrentMapCache, ConcurrentMapCacheManager}
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
@EnableCaching
class CachingConfig {

  @Bean
  def cacheManager(): CacheManager = {
    val cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(util.Arrays.asList(
      new ConcurrentMapCache(Constants.CACHE_NAME)
    ))
    cacheManager
  }

}
