package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

/**
 * Wraps the state updated properties: updated state and the event that triggered the state update.
 *
 * @param <S> The concrete type of the state object.
 */
public interface StateUpdated<S extends State> {

    /**
     * @return The event that triggered the state update.
     */
    Event getEvent();

    /**
     * @return The updated state. Note: may be equal to previous state.
     */
    S getState();

}
