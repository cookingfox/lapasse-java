package com.cookingfox.lapasse.api.event.exception;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.exception.LaPasseException;

/**
 * Thrown when an event handler returns null.
 */
public class EventHandlerReturnedNullException extends LaPasseException {

    public EventHandlerReturnedNullException(Event event) {
        super(String.format("Event handler returned null for event '%s'", event));
    }

}
