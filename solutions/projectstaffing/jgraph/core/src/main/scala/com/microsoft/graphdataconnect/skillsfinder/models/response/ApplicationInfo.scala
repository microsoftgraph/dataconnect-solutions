/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.response

case class ApplicationInfo(appName: String,
                           version: String,
                           builtAt: String,
                           buildBranch: String,
                           commit: String,
                           javaVersion: String,
                           scalaVersion: String,
                           dockerTag: String)
