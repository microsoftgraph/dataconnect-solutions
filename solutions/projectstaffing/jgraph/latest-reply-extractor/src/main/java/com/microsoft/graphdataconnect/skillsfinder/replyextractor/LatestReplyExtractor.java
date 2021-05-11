/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor;

import com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.EmailBodyParser;
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.html.HtmlEmailBodyParser;
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.text.PlainTextEmailBodyParser;

public class LatestReplyExtractor {

    public String getLatestMessageFromEmailThread(String emailBody, boolean parseEnronDataset, boolean isHtmlContent) throws Exception {
        EmailBodyParser emailBodyParser;
        if (isHtmlContent) {
            emailBodyParser = new HtmlEmailBodyParser();
        } else {
            emailBodyParser = new PlainTextEmailBodyParser();
        }
        return emailBodyParser.extractLatestMessageFromEmailBody(emailBody, parseEnronDataset);
    }

}
