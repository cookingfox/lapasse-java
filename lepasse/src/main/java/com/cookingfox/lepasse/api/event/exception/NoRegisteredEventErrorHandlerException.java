package com.cookingfox.lepasse.api.event.exception;

import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.exception.LePasseException;

/**
 * Thrown when there is no registered event error handler. Wraps the original exception.
 */
public class NoRegisteredEventErrorHandlerException extends LePasseException {

    public NoRegisteredEventErrorHandlerException(Throwable cause, Event event) {
        super(String.format("Exception thrown by handler for event '%s'", event), cause);
    }

}
