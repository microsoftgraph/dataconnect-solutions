/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.exceptions

case class PermissionConsentMissingException(message: String, exception: Throwable = null, consentRequestUrl: String) extends Exception(message, exception) {

}
