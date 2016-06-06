package com.cookingfox.lepasse.api.exception;

/**
 * Base exception class for LePasse exceptions.
 */
public class LePasseException extends RuntimeException {

    public LePasseException(String message) {
        super(message);
    }

    public LePasseException(String message, Throwable cause) {
        super(message, cause);
    }

    public LePasseException(Throwable cause) {
        super(cause);
    }

}
