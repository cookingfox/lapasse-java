package com.cookingfox.lapasse.api.logging;

import com.cookingfox.lapasse.api.command.logging.CommandLoggerAware;
import com.cookingfox.lapasse.api.event.logging.EventLoggerAware;
import com.cookingfox.lapasse.api.state.State;

/**
 * Combines a multitude of logger aware interfaces.
 *
 * @param <S> The concrete type of the state object.
 */
public interface LoggerAwareCollection<S extends State> extends
        CombinedLoggerAware<S>, CommandLoggerAware<S>, EventLoggerAware<S> {
}
