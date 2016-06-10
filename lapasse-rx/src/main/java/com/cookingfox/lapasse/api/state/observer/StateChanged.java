package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

/**
 * Wraps the state change properties: new state and the event that caused the state change.
 *
 * @param <S> The concrete type of the state object.
 */
public interface StateChanged<S extends State> {

    /**
     * @return The event that caused the state change.
     */
    Event getEvent();

    /**
     * @return The new state.
     */
    S getState();

}
