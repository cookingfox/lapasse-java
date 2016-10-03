package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

/**
 * Listener interface for when the state is updated.
 *
 * @param <S> The concrete type of the state object.
 */
public interface OnStateUpdated<S extends State> {

    /**
     * Called when an event updated the state.
     *
     * @param state The new state.
     * @param event The event which updated the state.
     */
    void onStateUpdated(S state, Event event);

}
