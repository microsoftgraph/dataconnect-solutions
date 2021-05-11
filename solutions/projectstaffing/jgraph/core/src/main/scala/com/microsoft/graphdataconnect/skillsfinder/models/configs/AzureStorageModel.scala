/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.configs

import java.time.OffsetDateTime

import org.apache.commons.lang3.StringUtils

case class AzureBlobStorageCredential(storageAccount: String, storageKey: String) {

  def validate(): Unit = {
    require(StringUtils.isNotBlank(storageAccount))
    require(StringUtils.isNotBlank(storageKey))
  }
}

case class SasKeyEntry(sasKey: String, expireOn: OffsetDateTime)

case class BlobExploreRequest(path: String, containerName: String) {
  require(path != null, "name must be defined")
  require(containerName != null, "container name must be defined")
  require(containerName.nonEmpty, "container name must not be empty")
}

case class BlobStorageResponse(blobFiles: List[AzureBlobResponseItem], sas: String)

case class AzureBlobResponseItem(
                                  name: String,
                                  isFolder: Boolean = false,
                                  creationTime: Option[OffsetDateTime] = None,
                                  lastModified: Option[OffsetDateTime] = None,
                                  contentLength: Option[Long] = None,
                                  contentType: Option[String] = None
                                )
