/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.config

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.IThrowableProxy

class CompressedStackTraceConverter extends ThrowableProxyConverter {

  override def throwableProxyToString(tp: IThrowableProxy): String = {
    val original = super.throwableProxyToString(tp)
    original.replaceAll("\n", "\t")
  }

}
