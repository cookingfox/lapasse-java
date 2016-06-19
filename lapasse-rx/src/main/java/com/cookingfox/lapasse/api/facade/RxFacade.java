package com.cookingfox.lapasse.api.facade;

import com.cookingfox.lapasse.api.command.bus.RxCommandBus;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;

/**
 * Extends the facade with Rx functionality.
 *
 * @param <S> The concrete type of the state object.
 */
public interface RxFacade<S extends State> extends
        RxCommandBus<S>,
        Facade<S>,
        RxStateObserver<S> {
}
