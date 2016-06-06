package com.cookingfox.lepasse.api.event.exception;

import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.exception.LePasseException;

/**
 * Thrown when an event handler returns null.
 */
public class EventHandlerReturnedNullException extends LePasseException {

    public EventHandlerReturnedNullException(Event event) {
        super(String.format("Event handler returned null for event '%s'", event));
    }

}
