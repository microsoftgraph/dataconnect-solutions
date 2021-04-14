package com.microsoft.graphdataconnect.skillsfinder.replyextractor.regex;

public class SpanishQuoteHeaderRegex extends QuoteHeaderRegex {
    public SpanishQuoteHeaderRegex() {
        super();
    }

    @Override
    public String on() {
        return "El";
    }

    @Override
    public String wrote() {
        return "escribió";
    }

    @Override
    public String to() {
        return "Para";
    }

    @Override
    public String from() {
        return "De";
    }

    @Override
    public String subject() {
        return "Asunto";
    }

    @Override
    public String sent() {
        return "Enviado";
    }

    @Override
    public String date() {
        return "Fecha";
    }
}
