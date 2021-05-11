/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.html;

import com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.EmailBodyParser;
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.text.PlainTextEmailBodyParser;
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.utils.HtmlToTextConverter;

public class HtmlEmailBodyParser implements EmailBodyParser {
    PlainTextEmailBodyParser plainTextEmailBodyParser;

    public HtmlEmailBodyParser() {
        plainTextEmailBodyParser = new PlainTextEmailBodyParser();
    }

    @Override
    public String extractLatestMessageFromEmailBody(String emailBody, boolean parseEnronDataset) throws Exception {
        String plainTextBody = HtmlToTextConverter.extractTextFromHtml(emailBody);
        return plainTextEmailBodyParser.extractLatestMessageFromEmailBody(plainTextBody, parseEnronDataset);
    }
}
