/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser;

public interface EmailBodyParser {
    String extractLatestMessageFromEmailBody(String emailBody, boolean parseEnronDataset) throws Exception;
}
