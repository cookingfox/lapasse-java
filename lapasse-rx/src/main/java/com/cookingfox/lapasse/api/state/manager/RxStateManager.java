package com.cookingfox.lapasse.api.state.manager;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;

/**
 * Manages the state object.
 *
 * @param <S> The concrete type of the state object.
 */
public interface RxStateManager<S extends State> extends RxStateObserver<S>, StateManager<S> {

}
