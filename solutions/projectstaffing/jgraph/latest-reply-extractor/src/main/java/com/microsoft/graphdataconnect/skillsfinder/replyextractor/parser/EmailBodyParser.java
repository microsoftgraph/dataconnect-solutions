package com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser;

public interface EmailBodyParser {
    String extractLatestMessageFromEmailBody(String emailBody, boolean parseEnronDataset) throws Exception;
}
