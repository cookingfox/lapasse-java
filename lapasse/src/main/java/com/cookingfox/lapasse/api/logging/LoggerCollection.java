package com.cookingfox.lapasse.api.logging;

import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.state.State;

/**
 * Combines a multitude of logger interfaces.
 *
 * @param <S> The concrete type of the state object.
 */
public interface LoggerCollection<S extends State> extends
        LoggerAwareCollection<S>,
        CommandLogger<S>,
        EventLogger<S> {
}
