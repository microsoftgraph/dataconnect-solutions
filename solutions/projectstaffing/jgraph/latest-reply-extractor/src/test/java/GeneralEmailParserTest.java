/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import com.microsoft.graphdataconnect.skillsfinder.replyextractor.LatestReplyExtractor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeneralEmailParserTest {

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenReplyHeaderHasOneLine() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("general_text_emails/one_line_english_header.txt"), false, false);
        assertEquals(
                "2 to 1, 3\n" +
                        "222\n" +
                        "\n", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenReplyStartsWithGreaterThanSymbol() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("general_text_emails/one_line_english_header_greater_than.txt"), false, false);
        assertEquals(
                "Payload inserted\n" +
                        "\n" +
                        "John\n" +
                        "\n" +
                        "Sent from my iPhone\n" +
                        "\n", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenReplyHeaderHasClassicTypeFromToSubject() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("general_html_emails/email1.txt"), false, true);
        assertEquals(
                "2 to 3, 1\n" +
                        "444\n" +
                        "\n", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenReplyHeaderHasOneLineInHTML() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("general_html_emails/email2.txt"), false, true);
        assertEquals(
                "\\r\\n\n" +
                        "\n" +
                        "\n" +
                        "dhiwandwad Dwjandjwa\n" +
                        "\n" +
                        "\n" +
                        "\n", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenReplyHeaderHasItTypeFromSubjectDateTo() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("general_html_emails/from_subject_date_to.txt"), true, true);
        assertEquals(
                "Begin forwarded message:\n" +
                        "\n", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenReplyHeaderHasItTypeFromSentToSubject() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("general_text_emails/from_sent_to_subject.txt"), true, true);
        assertEquals(
                "3 to 2, 1\n" +
                        "\n" +
                        "333\n" +
                        "\n" +
                        "\n" +
                        "\n", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenReplyHeaderFromSubjectDateTo() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("general_text_emails/from_subject_date_to.txt"), true, false);
        assertEquals("Denis, please reply to this emails. Any payload...\n" +
                "\n" +
                "Begin forwarded message:", message.trim());
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenReplyHeaderDeLaTrimisCatreSubiect() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("general_text_emails/de_la_trimis_catre_subiect.txt"), true, false);
        assertEquals("7th Reply to Richard Doe and Andrei", message.trim());
    }

}
