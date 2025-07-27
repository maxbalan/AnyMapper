package com.moftium.anymapper.exception;

public class AnyMapperException extends Exception {
    public AnyMapperException(String message) {
        super(message);
    }

    public AnyMapperException(Throwable cause) {
        super(cause);
    }

    public AnyMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
