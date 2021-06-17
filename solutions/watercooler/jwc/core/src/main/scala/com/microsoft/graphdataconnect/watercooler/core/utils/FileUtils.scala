/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.utils

import java.io._

object FileUtils {


  def readResourceContent(relativePath: String): String = {

    val classLoader: ClassLoader = FileUtils.getClass.getClassLoader

    val resource: InputStream = classLoader.getResourceAsStream(relativePath)

    if (resource == null) {
      //      logger.error("Can't find resource  '" + relativePath + "' in classpath ")
      throw new IllegalArgumentException(relativePath + " resource is not found in classpath")
    } else {
      try {
        val reader: InputStreamReader = new InputStreamReader(resource)
        val br: BufferedReader = new BufferedReader(reader)
        val sb: StringBuilder = new StringBuilder()
        var line: String = br.readLine()
        while (line != null) {
          sb.append(line)
          line = br.readLine()
        }
        sb.toString()
      } catch {
        case e: FileNotFoundException =>
          e.printStackTrace()
          //          logger.error("Can't find resource  " + relativePath + " in classpath ", e)
          throw new IllegalArgumentException("file is not found " + relativePath)
        case e: IOException =>
          e.printStackTrace()
          throw new IllegalArgumentException("Can't read content of file " + relativePath)
      } finally {
        resource.close()
      }
    }

  }

}
