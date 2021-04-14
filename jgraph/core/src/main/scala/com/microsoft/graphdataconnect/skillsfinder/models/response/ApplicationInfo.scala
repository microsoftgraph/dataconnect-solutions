package com.microsoft.graphdataconnect.skillsfinder.models.response

case class ApplicationInfo(appName: String,
                           version: String,
                           builtAt: String,
                           buildBranch: String,
                           commit: String,
                           javaVersion: String,
                           scalaVersion: String,
                           dockerTag: String)
