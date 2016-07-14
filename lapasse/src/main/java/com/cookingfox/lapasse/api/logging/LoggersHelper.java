package com.cookingfox.lapasse.api.logging;

import com.cookingfox.lapasse.api.command.logging.CommandLoggerHelper;
import com.cookingfox.lapasse.api.event.logging.EventLoggerHelper;
import com.cookingfox.lapasse.api.state.State;

/**
 * Combines a multitude of logger interfaces.
 *
 * @param <S> The concrete type of the state object.
 */
public interface LoggersHelper<S extends State> extends
        CombinedLoggerHelper<S>, CommandLoggerHelper, EventLoggerHelper<S> {
}
