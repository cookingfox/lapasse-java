package com.cookingfox.lapasse.api.event.logging;

import com.cookingfox.lapasse.api.state.State;

/**
 * Helper interface for event logger.
 *
 * @param <S> The concrete type of the state object.
 */
public interface EventLoggerHelper<S extends State> extends EventLogger<S>, EventLoggerAware<S> {
}
