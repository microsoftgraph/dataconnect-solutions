/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import com.microsoft.graphdataconnect.skillsfinder.replyextractor.LatestReplyExtractor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnronEmailParserTest {

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("enron_text_emails/email1.txt"), true, false);
        assertEquals(message,
                "OK Kevin, count me in for Agenda Item No. 5. Please advise as to the scope and\n" +
                        "nature of the desired components for this Agenda Item, or perhaps pass my offer\n" +
                        "along to Orlando and we can then work together to ensure a high quality panel /\n" +
                        "presentation to cover the \"\"New Supply Sources\"\"...... wayne\n" +
                        "\n" +
                        "Talk to you soon............... wayne\n" +
                        "\n" +
                        "\n" +
                        "\n\"\"Hyatt,\n" +
                        "<Kevin.Hyatt@        cc:     \"\"Carrie Shapiro (E-mail)\"\" <carries@tradefairgroup.com>");

    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenHeaderBeginsWithOriginalMessage() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("enron_text_emails/email2.txt"), true, false);
        assertEquals(
                "Hi Wayne, thanks for the reply.  It would be great if you could develop\n" +
                        "something for item 5.  I think Orlando Alvarado is having difficulty\n" +
                        "getting anyone from Williams signed up for it.  As for item 6, I may\n" +
                        "have an interested party for that one but I'll keep you posted.\n" +
                        "\n" +
                        "thanks again,\n" +
                        "kh\n\n", message);

    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenHeaderIsVeryUnstructured() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("enron_text_emails/email3.txt"), true, false);
        assertEquals(
                "Kevin, we have not yet had the opportunity to meet as I have not been\n" +
                        "able to\n" +
                        "make it to the Committee meetings as Carrie had set-up. Taking a look at\n" +
                        "the\n" +
                        "Agenda, I can probably help with items 5 or 6 and would be happy to\n" +
                        "develop and\n" +
                        "deliver a presentation or would ensure that a senior representative from\n" +
                        "Enbridge would do that. My natural tendency (with my current position in\n" +
                        "Northern Pipeline Development) would be towards the Agenda Item No. 5\n" +
                        "(New\n" +
                        "Supply  Sources), but I probably could find a good resource within\n" +
                        "Enbridge to\n" +
                        "present some views and thinking on the Infrastructure De-bottlenecking\n" +
                        "item.\n" +
                        "\n" +
                        "Please let me know what you think............ wayne\n" +
                        "\n" +
                        "I look forward to the Conference and to getting a chance to meet you.\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\"\"Hyatt,\n" +
                        "\n" +
                        "<Kevin.Hyatt@", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenHeaderStartsWithForward() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("enron_text_emails/email4.txt"), true, false);
        assertEquals(
                "\n" +
                        "Seabron --\n" +
                        "\n" +
                        "Here is a paper Brattle is putting together for Enron in Japan.  See Section\n" +
                        "5 - A Japanese Pool.  They make several good arguments as to why a mandatory\n" +
                        "pool with financial settlements around the pool is key for Japanese\n" +
                        "liberalization.   Maybe steal some of their ideas for our US policy arguments.\n" +
                        "\n" +
                        "Jim\n" +
                        "\n" +
                        "\n" +
                        "\n", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenHeaderBeginsWithEnronSpecificStructuredHeader() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("enron_text_emails/email5.txt"), true, false);
        assertEquals(
                "Jim - this is what we discussed in Houston last week, i.e., a proposal for a\n" +
                        "market in Japan.  Any thoughts would be appreciated, I am sure, by Mark\n" +
                        "Crowther and Nick O'Day.   thanks  mcs\n", message);
    }

    @Test
    public void testFirstMessageFromReplyIsExtractedCorrectly_WhenHeaderHasAddressesOnMultipleLines() throws Exception {
        LatestReplyExtractor latestReplyExtractor = new LatestReplyExtractor();
        String message = latestReplyExtractor.getLatestMessageFromEmailThread(FixtureGetter.getFixture("enron_text_emails/email6.txt"), true, false);
        assertEquals(
                "Jim -- We need to have a follow-up to our conference call.  Several items\n" +
                        "need leads and we need to finalize who will be the lead of number 7, as we\n" +
                        "discussed.  Also need to include Ray Alvarez on the distribution list.\n" +
                        "Thanks Joe\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "James D Steffes\n" +
                        "03/12/2001 05:51 PM\n" +
                        "\n", message);
    }

}
