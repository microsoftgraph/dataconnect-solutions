/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.fs

import java.net.URI

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkContext

class FileSystemInitializer(val connectionString: String)
                           (implicit val sparkContext: SparkContext) {

  private var currentFileSystem: Option[FileSystem] = None

  def fileSystem: FileSystem = currentFileSystem.getOrElse({
    val newFileSystem = FileSystem.get(URI.create(connectionString), sparkContext.hadoopConfiguration)
    currentFileSystem = Some(newFileSystem)
    newFileSystem
  })

}

object FileSystemInitializer {
  def apply(connectionString: String)(implicit sparkContext: SparkContext): FileSystemInitializer = new FileSystemInitializer(connectionString)
}


