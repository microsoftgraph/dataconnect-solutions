/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.employee

case class EmployeeResponseWrapper(reachedEndOfResults: Boolean,
                                   employees: Seq[Employee])
