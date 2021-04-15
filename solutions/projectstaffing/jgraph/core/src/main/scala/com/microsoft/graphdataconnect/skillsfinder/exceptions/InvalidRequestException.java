package com.microsoft.graphdataconnect.skillsfinder.exceptions;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String msg) {
        super(msg);
    }

    public InvalidRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
