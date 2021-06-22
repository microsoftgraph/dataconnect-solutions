/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */


package com.microsoft.graphdataconnect.watercooler.profiles.extractor.fs

import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkContext

import scala.util.Try

class FileSystemInitializer(val connectionString: String)
                           (implicit val sparkContext: SparkContext) {

  private var currentFileSystem: Option[FileSystem] = None

  def fileSystem: FileSystem = currentFileSystem.getOrElse({
    val newFileSystem = FileSystem.get(URI.create(connectionString), sparkContext.hadoopConfiguration)
    currentFileSystem = Some(newFileSystem)
    newFileSystem
  })

  def getAzureStorageFileSystem(container: String, storageName: String, storageKey: String): FileSystem = {
    val configObj = new Configuration()
//    val storageAccountNamePlusPaths = connectionUrl.trim.replace("wasbs://", "").split("/")
//    val finalConnectionUrl = "wasbs://" + storageAccountNamePlusPaths.head + "/" + storageAccountNamePlusPaths.tail.map(parts => UrlEscapers.urlFragmentEscaper.escape(parts)).mkString("/")
    configObj.set("fs.defaultFS", s"wasbs://$container@$storageName.blob.core.windows.net")
    configObj.set("fs.azure.account.key." + storageName + ".blob.core.windows.net",
      storageKey)
    Try(FileSystem.newInstance(configObj)).getOrElse(throw new Exception(s"Cannot access storage"))
  }

}

object FileSystemInitializer {
  def apply(connectionString: String)(implicit sparkContext: SparkContext): FileSystemInitializer = new FileSystemInitializer(connectionString)
}
