package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.state.State;
import rx.Observable;

/**
 * Extends the state observer with Rx functionality.
 *
 * @param <S> The concrete type of the state object.
 */
public interface RxStateObserver<S extends State> extends StateObserver<S> {

    /**
     * Returns an Rx observable for state changes.
     *
     * @return An Rx observable for state changes.
     */
    Observable<StateChanged<S>> observeStateChanges();

}
