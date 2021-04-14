package com.microsoft.graphdataconnect.skillsfinder.replyextractor.utils;

import org.apache.commons.io.FileUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HtmlToTextConverter {
    public static String extractTextFromHtml(String html) throws Exception {
        BodyContentHandler handler = new BodyContentHandler();

        Metadata metadata = new Metadata();

        InputStream inputstream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        ParseContext pcontext = new ParseContext();

        //Html parser
        HtmlParser htmlparser = new HtmlParser();

        htmlparser.parse(inputstream, handler, metadata, pcontext);

        String email = handler.toString();
        return email;
    }

    public static String extractTextFromHtmlInFile(String filePath) throws Exception {
        File file = new File(filePath);
        String emailContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        extractTextFromHtml(emailContent);
        return emailContent;
    }
}
