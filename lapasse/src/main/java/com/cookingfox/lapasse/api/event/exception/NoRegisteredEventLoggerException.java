package com.cookingfox.lapasse.api.event.exception;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.exception.LaPasseException;

/**
 * Thrown when there is no registered event logger. Wraps the original exception.
 */
public class NoRegisteredEventLoggerException extends LaPasseException {

    public NoRegisteredEventLoggerException(Throwable cause, Event event) {
        super(String.format("Exception thrown by handler for event '%s'", event), cause);
    }

}
