package com.cookingfox.lapasse.api.facade;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;

/**
 * Provides access to the core functionality.
 *
 * @param <S> The concrete type of the state object.
 */
public interface RxFacade<S extends State> extends Facade<S>, RxStateObserver<S> {
}
