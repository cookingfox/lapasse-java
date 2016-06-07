package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.state.State;

/**
 * Observes the state object.
 *
 * @param <S> The concrete type of the state object.
 */
public interface StateObserver<S extends State> {

    /**
     * Returns the current state.
     *
     * @return The current state.
     */
    S getCurrentState();

    /**
     * Subscribe to when the state changes.
     *
     * @param subscriber The subscriber to notify when the state changes.
     */
    void subscribe(OnStateChanged<S> subscriber);

    /**
     * Remove previously added subscriber.
     *
     * @param subscriber The subscriber to remove.
     */
    void unsubscribe(OnStateChanged<S> subscriber);

}
