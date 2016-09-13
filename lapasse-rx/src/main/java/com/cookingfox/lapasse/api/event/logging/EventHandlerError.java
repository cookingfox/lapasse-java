package com.cookingfox.lapasse.api.event.logging;

import com.cookingfox.lapasse.api.event.Event;

/**
 * Wraps the event handler error.
 */
public interface EventHandlerError {

    /**
     * @return TThe error that occurred.
     */
    Throwable getError();

    /**
     * @return The event that was handled.
     */
    Event getEvent();

}
