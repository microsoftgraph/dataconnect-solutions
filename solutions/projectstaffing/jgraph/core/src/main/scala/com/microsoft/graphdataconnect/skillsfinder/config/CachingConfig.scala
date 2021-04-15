package com.microsoft.graphdataconnect.skillsfinder.config

import java.util

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
@EnableCaching
class CachingConfig {

  @Bean
  def cacheManager(): CacheManager = {
    val cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(util.Arrays.asList(
      new ConcurrentMapCache(CachingConfig.CACHE_NAME)
    ))
    cacheManager
  }

}

object CachingConfig {
  //has to be final in order to used it with @Cacheble and @CachePut annotations in ConfigurationService
  final val CACHE_NAME = "jGraph Cache"
}
