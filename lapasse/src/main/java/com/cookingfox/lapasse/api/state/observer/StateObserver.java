package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.state.State;

/**
 * Observes the state object.
 *
 * @param <S> The concrete type of the state object.
 */
public interface StateObserver<S extends State> {

    /**
     * Add listener for when the state changed.
     *
     * @param listener The listener to notify when the state changes.
     */
    void addStateChangedListener(OnStateChanged<S> listener);

    /**
     * Returns the current state.
     *
     * @return The current state.
     */
    S getCurrentState();

    /**
     * Remove previously added state changed listener.
     *
     * @param listener The listener to remove.
     */
    void removeStateChangedListener(OnStateChanged<S> listener);

}
