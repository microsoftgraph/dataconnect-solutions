/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.config.serialization

import com.fasterxml.jackson.core.`type`.TypeReference
import com.microsoft.graphdataconnect.model.admin.IngestionMode

class IngestionModeType extends TypeReference[IngestionMode.type]
