package com.cookingfox.lepasse.api.facade;

import com.cookingfox.lepasse.api.command.bus.CommandBus;
import com.cookingfox.lepasse.api.event.bus.EventBus;
import com.cookingfox.lepasse.api.state.State;
import com.cookingfox.lepasse.api.state.observer.StateObserver;

/**
 * Provides access to the core functionality.
 *
 * @param <S> The concrete type of the state object.
 */
public interface Facade<S extends State> extends CommandBus<S>, EventBus<S>, StateObserver<S> {
}
