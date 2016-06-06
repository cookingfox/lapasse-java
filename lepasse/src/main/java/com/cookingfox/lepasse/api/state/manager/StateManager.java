package com.cookingfox.lepasse.api.state.manager;

import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.state.State;
import com.cookingfox.lepasse.api.state.observer.StateObserver;

/**
 * Manages the state object.
 *
 * @param <S> The concrete type of the state object.
 */
public interface StateManager<S extends State> extends StateObserver<S> {

    /**
     * Validate and set a new state object. Calls subscribers if the new state is different from the
     * previous state.
     *
     * @param newState The new state object to set.
     * @param event    The event that triggered the state change.
     */
    void handleNewState(S newState, Event event);

}
