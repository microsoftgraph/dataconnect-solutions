/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.login

import collection.JavaConverters._

case class GraphApiAppAccessInfo(clientId: String, clientSecret: String, directoryId: String, scope: java.util.List[String] = List("https://graph.microsoft.com/.default").asJava)
