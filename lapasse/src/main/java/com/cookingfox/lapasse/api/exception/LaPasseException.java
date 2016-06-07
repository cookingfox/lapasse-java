package com.cookingfox.lapasse.api.exception;

/**
 * Base exception class for LaPasse exceptions.
 */
public class LaPasseException extends RuntimeException {

    public LaPasseException(String message) {
        super(message);
    }

    public LaPasseException(String message, Throwable cause) {
        super(message, cause);
    }

    public LaPasseException(Throwable cause) {
        super(cause);
    }

}
