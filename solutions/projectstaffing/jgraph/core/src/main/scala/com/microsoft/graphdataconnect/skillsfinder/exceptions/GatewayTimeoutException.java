/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.exceptions;

public class GatewayTimeoutException extends RuntimeException {

    public GatewayTimeoutException(String msg) {
        super(msg);
    }
}
