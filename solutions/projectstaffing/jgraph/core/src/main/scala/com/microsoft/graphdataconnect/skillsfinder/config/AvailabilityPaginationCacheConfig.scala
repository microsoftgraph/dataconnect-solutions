/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.config

import com.microsoft.graphdataconnect.skillsfinder.models.business.{AvailabilitySearchStartPoint, SearchQueryIdentifier}
import org.ehcache.Cache
import org.ehcache.config.builders.{CacheConfigurationBuilder, CacheManagerBuilder, ExpiryPolicyBuilder, ResourcePoolsBuilder}
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class AvailabilityPaginationCacheConfig {

  val AVAILABILITY_PAGINATION_CACHE_NAME: String = "availability"

  @Value("${availability.pagination.cache.expiration.time.seconds}")
  var expirationTimeSeconds: Int = _

  @Value("${availability.pagination.cache.number.entries}")
  var numberOfEntriesInCache: Int = _

  @Bean
  def availabilityPaginationCache(): Cache[SearchQueryIdentifier, AvailabilitySearchStartPoint] = {

    val cacheConfigurationBuilder = CacheConfigurationBuilder
      .newCacheConfigurationBuilder(classOf[SearchQueryIdentifier], classOf[AvailabilitySearchStartPoint], ResourcePoolsBuilder.heap(numberOfEntriesInCache))
      .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(java.time.Duration.ofSeconds(expirationTimeSeconds)))

    val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
      .withCache(AVAILABILITY_PAGINATION_CACHE_NAME, cacheConfigurationBuilder)
      .build()

    cacheManager.init()
    cacheManager.getCache(AVAILABILITY_PAGINATION_CACHE_NAME, classOf[SearchQueryIdentifier], classOf[AvailabilitySearchStartPoint])
  }

}
