/*
The MIT License (MIT)

Copyright (c) Edlio Inc

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.text;

import com.google.common.collect.ImmutableList;
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.EmailBodyParser;
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.regex.HeaderRegexFactory;
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.regex.Language;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlainTextEmailBodyParser implements EmailBodyParser {

    static final Pattern SIG_PATTERN = Pattern.compile("((^Sent from my (\\s*\\w+){1,3}$)|(^-\\w|^\\s?__|^\\s?--|^\u2013|^\u2014))", Pattern.DOTALL);
    static final Pattern QUOTE_PATTERN = Pattern.compile("(^>+)", Pattern.DOTALL);
    private static List<Pattern> compiledQuoteHeaderPatterns;

    private List<String> quoteHeadersRegex = new ArrayList<String>();
    private List<FragmentDTO> fragments = new ArrayList<FragmentDTO>();
    private int maxParagraphLines;
    private int maxNumCharsEachLine;


    /**
     * Initialize PlainTextEmailBodyParser.
     */
    public PlainTextEmailBodyParser() {
        compiledQuoteHeaderPatterns = new ArrayList<Pattern>();
        maxParagraphLines = 6;
        maxNumCharsEachLine = 400;
    }

    @Override
    public String extractLatestMessageFromEmailBody(String emailBody, boolean parseEnronDataset) throws Exception {
        Email a = parse(emailBody, parseEnronDataset);
        return a.getFragments().get(0).getContent();
    }

    public Email parse(String emailText, boolean parseEnronData) {
        fragments = new ArrayList<FragmentDTO>();
        quoteHeadersRegex = HeaderRegexFactory.getQuoteHeaderRegexFor(parseEnronData, Language.values());

        compileQuoteHeaderRegexes();

        // Normalize line endings
        emailText = emailText.replace("\r\n", "\n");
        //remove special characters from each line

        emailText = emailText.replace("\uFEFF", "");
        emailText = emailText.replace("\u00a0", "");
        emailText = emailText.replace("\u00B7", "");
        emailText = emailText.replace("\u22C5", "");
        emailText = emailText.replace("\u0387", "");

        //remove leading spaces
        emailText = emailText.replaceAll("(?m)(^((\\t+)|( +)))", "");

        //replace "> blabla" or ">> blabla" with "bla.."
        emailText = emailText.replaceAll("(?m)(^>+[ ])", "");
        //replace ">\n" or ">>\n" with "\n"
        emailText = emailText.replaceAll("(?m)(^>+$)", "");


        // Split body to multiple lines.
        String[] lines = new StringBuilder(emailText).toString().split("\n");

        /* Paragraph for multi-line quote headers.
         * Some clients break up the quote headers into multiple lines.
         */
        List<String> paragraph = new ArrayList<String>();

        int i = 0;

        // Scans the given email line by line and figures out which fragment it belong to.
        for (String line : lines) {
            i++;
            // Strip new line at the end of the string
            line = StringUtils.stripEnd(line, "\n");
            // Strip empty spaces at the end of the string
            line = StringUtils.stripEnd(line, null);

            String patternMatchResult = isQuoteHeader(paragraph);

            if (patternMatchResult != null) {
                FragmentDTO fragment = new FragmentDTO();
                fragment.lines = ImmutableList.of(patternMatchResult);
                fragments.add(fragment);
                paragraph = new ArrayList<>();
                break;
            }

            paragraph.add(line);
        }
        if (!paragraph.isEmpty()) {
            FragmentDTO fragment = new FragmentDTO();
            Collections.reverse(paragraph);
            fragment.lines = paragraph;
            fragments.add(fragment);
        }

        return createEmail(fragments);
    }


    /**
     * Returns existing quote headers regular expressions.
     *
     * @return
     */
    public List<String> getQuoteHeadersRegex() {
        return this.quoteHeadersRegex;
    }

    /**
     * Sets quote headers regular expressions.
     *
     * @param newRegex
     */
    public void setQuoteHeadersRegex(List<String> newRegex) {
        this.quoteHeadersRegex = newRegex;
    }

    /**
     * Gets max number of lines allowed for each paragraph when checking quote headers.
     *
     * @return
     */
    public int getMaxParagraphLines() {
        return this.maxParagraphLines;
    }

    /**
     * Sets max number of lines allowed for each paragraph when checking quote headers.
     *
     * @param maxParagraphLines
     */
    public void setMaxParagraphLines(int maxParagraphLines) {
        this.maxParagraphLines = maxParagraphLines;
    }

    /**
     * Gets max number of characters allowed for each line when checking quote headers.
     *
     * @return
     */
    public int getMaxNumCharsEachLine() {
        return maxNumCharsEachLine;
    }

    /**
     * Sets max number of characters allowed for each line when checking quote headers.
     *
     * @param maxNumCharsEachLine
     */
    public void setMaxNumCharsEachLine(int maxNumCharsEachLine) {
        this.maxNumCharsEachLine = maxNumCharsEachLine;
    }

    /**
     * Creates {@link Email} object from List of fragments.
     *
     * @param fragmentDTOs
     * @return
     */
    protected Email createEmail(List<FragmentDTO> fragmentDTOs) {
        List<Fragment> fs = new ArrayList<Fragment>();
        Collections.reverse(fragmentDTOs);
        for (FragmentDTO f : fragmentDTOs) {
            Collections.reverse(f.lines);
            String content = new StringBuilder(StringUtils.join(f.lines, "\n")).toString();
            Fragment fr = new Fragment(content, f.isHidden, f.isSignature, f.isQuoted);
            fs.add(fr);
        }
        return new Email(fs);
    }

    /**
     * Compile all the quote headers regular expressions before the parsing.
     */
    private void compileQuoteHeaderRegexes() {
        for (String regex : quoteHeadersRegex) {
            compiledQuoteHeaderPatterns.add(Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL));
        }
    }

    /**
     * Check if the line is a signature.
     *
     * @param line
     * @return
     */
    private boolean isSignature(String line) {
        boolean find = SIG_PATTERN.matcher(line).find();
        return find;
    }

    /**
     * Checks if the line is quoted line.
     *
     * @param line
     * @return
     */
    private boolean isQuote(String line) {
        return QUOTE_PATTERN.matcher(line).find();
    }

    /**
     * Checks if lines in the fragment are empty.
     *
     * @param fragment
     * @return
     */
    private boolean isEmpty(FragmentDTO fragment) {
        return StringUtils.join(fragment.lines, "").isEmpty();
    }

    /**
     * If the line matches the current fragment, return true.
     * Note that a common reply header also counts as part of the quoted Fragment,
     * even though it doesn't start with `>`.
     *
     * @param fragment
     * @param line
     * @param isQuoted
     * @return
     */
//	private boolean isFragmentLine(FragmentDTO fragment, String line, boolean isQuoted) {
//		return fragment.isQuoted == isQuoted || (fragment.isQuoted && (isQuoteHeader(Arrays.asList(line)) || line.isEmpty()));
//	}

    /**
     * Add fragment to fragments list.
     *
     * @param fragment
     */
    private void addFragment(FragmentDTO fragment) {
        if (fragment.isQuoted || fragment.isSignature || isEmpty(fragment))
            fragment.isHidden = true;

        fragments.add(fragment);
    }

    /**
     * Checks if the given multiple-lines paragraph has one of the quote headers.
     * Returns false if it doesn't contain any of the quote headers,
     * if paragraph lines are greater than maxParagraphLines, or line has more than maxNumberCharsEachLine characters.
     *
     * @param paragraph
     * @return
     */
    private String isQuoteHeader(List<String> paragraph) {
        List<String> paragraphCopy = new ArrayList<>(paragraph);

//		Collections.reverse(paragraphCopy);
        String content = new StringBuilder(StringUtils.join(paragraphCopy, "\n")).toString();
        for (Pattern p : compiledQuoteHeaderPatterns) {
            Matcher matcher = p.matcher(content);
            if (matcher.find()) {
                content = content.replaceAll("(?m)(" + p.pattern() + ")", "");
                return content;
            }
        }

        return null;

    }
}
