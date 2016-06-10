package com.cookingfox.lapasse.api.facade;

import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.logging.CombinedLoggerAware;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.StateObserver;

/**
 * Provides access to the core functionality.
 *
 * @param <S> The concrete type of the state object.
 */
public interface Facade<S extends State> extends
        CombinedLoggerAware<S>,
        CommandBus<S>,
        EventBus<S>,
        StateObserver<S> {
}
