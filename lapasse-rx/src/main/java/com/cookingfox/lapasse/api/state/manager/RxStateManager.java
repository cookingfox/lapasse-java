package com.cookingfox.lapasse.api.state.manager;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;

/**
 * Extends the state manager with Rx functionality.
 *
 * @param <S> The concrete type of the state object.
 */
public interface RxStateManager<S extends State> extends RxStateObserver<S>, StateManager<S> {
}
