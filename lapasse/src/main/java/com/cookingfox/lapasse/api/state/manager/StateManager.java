package com.cookingfox.lapasse.api.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.lifecycle.Disposable;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.StateObserver;

/**
 * Manages the state object.
 *
 * @param <S> The concrete type of the state object.
 */
public interface StateManager<S extends State> extends Disposable, StateObserver<S> {

    void handleNewState(S newState);

    void handleNewState(S newState, String event);

    /**
     * Validate and set a new state object. Notifies listeners if the new state is different from
     * the previous state.
     *
     * @param newState The new state object to set.
     * @param event    The event that triggered the state change.
     */
    void handleNewState(S newState, Event event);

}
