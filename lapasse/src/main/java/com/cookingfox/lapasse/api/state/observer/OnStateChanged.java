package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

/**
 * Listener interface for when the state changes.
 *
 * @param <S> The concrete type of the state object.
 */
public interface OnStateChanged<S extends State> {

    /**
     * Called when an event causes the state to change.
     *
     * @param state The concrete state object.
     * @param event The event that changed the state.
     */
    void onStateChanged(S state, Event event);

}
