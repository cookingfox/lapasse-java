package com.cookingfox.lapasse.api.event.logging;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

/**
 * Wraps the event handler result.
 *
 * @param <S> The concrete type of the state object.
 */
public interface EventHandlerResult<S extends State> {

    /**
     * @return The event that was handled.
     */
    Event getEvent();

    /**
     * @return The new state that was returned by the event handler.
     */
    S getNewState();

}
