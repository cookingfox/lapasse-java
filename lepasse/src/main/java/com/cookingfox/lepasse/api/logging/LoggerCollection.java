package com.cookingfox.lepasse.api.logging;

import com.cookingfox.lepasse.api.command.logging.CommandLogger;
import com.cookingfox.lepasse.api.event.logging.EventLogger;
import com.cookingfox.lepasse.api.state.State;

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
