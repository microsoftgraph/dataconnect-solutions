package com.microsoft.graphdataconnect.skillsfinder.replyextractor.parser.text;

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

public class Fragment {
    private String content;
    private boolean isHidden;
    private boolean isSignature;
    private boolean isQuoted;

    public Fragment(String content, boolean isHidden, boolean isSignature, boolean isQuoted) {
        this.content = content;
        this.isHidden = isHidden;
        this.isSignature = isSignature;
        this.isQuoted = isQuoted;
    }

    public String getContent() {
        return content;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean isSignature() {
        return isSignature;
    }

    public boolean isQuoted() {
        return isQuoted;
    }

    public boolean isEmpty() {
        return "".equals(this.content.replace("\n", ""));
    }
}
