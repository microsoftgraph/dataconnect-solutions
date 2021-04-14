package com.microsoft.graphdataconnect.skillsfinder.config

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.multipart.commons.CommonsMultipartResolver

@Configuration
class FileUploadConfig {

  @Bean(name = Array("multipartResolver")) def multipartResolver: CommonsMultipartResolver = {
    val multipartResolver = new CommonsMultipartResolver
    multipartResolver.setMaxUploadSize(-1)
    multipartResolver.setMaxUploadSizePerFile(-1)

    // Set to 100MB the maximum allowed size (in bytes) above which uploads are written to disk.
    // MultipartFile objects are stored on disk when this limit is passed.
    // Because we want to copy the csv HR Data file asynchronously, we have to save the file in memory.
    // Otherwise, if the file was saved on disk, when the request finishes the temporary file would be deleted.
    multipartResolver.setMaxInMemorySize(104857600) // 100MB

    multipartResolver
  }

}
