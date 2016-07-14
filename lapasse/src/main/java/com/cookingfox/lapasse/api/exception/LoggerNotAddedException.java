package com.cookingfox.lapasse.api.exception;

/**
 * Thrown when an attempt is made to remove a logger that was not added.
 */
public class LoggerNotAddedException extends LaPasseException {

    public LoggerNotAddedException(Object logger, Object target) {
        super(String.format("Could not remove logger '%s' that was not added to '%s'",
                logger, target));
    }

}
