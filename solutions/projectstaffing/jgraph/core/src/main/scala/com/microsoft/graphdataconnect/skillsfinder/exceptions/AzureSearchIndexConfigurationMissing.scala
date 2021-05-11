/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.exceptions

class AzureSearchIndexConfigurationMissing(indexConfigurationType: String) extends RuntimeException(s"Missing Azure Search $indexConfigurationType index configuration") {

}
