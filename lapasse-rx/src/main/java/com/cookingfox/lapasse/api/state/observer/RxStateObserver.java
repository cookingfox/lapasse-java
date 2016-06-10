package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.state.State;
import rx.Observable;

/**
 * Observes the state object.
 *
 * @param <S> The concrete type of the state object.
 */
public interface RxStateObserver<S extends State> extends StateObserver<S> {

    Observable<StateChanged<S>> observeStateChanges();

}
