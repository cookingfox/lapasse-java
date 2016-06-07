package com.cookingfox.lepasse.api.logging;

import com.cookingfox.lepasse.api.command.logging.CommandLogger;
import com.cookingfox.lepasse.api.command.logging.CommandLoggerAware;
import com.cookingfox.lepasse.api.event.logging.EventLogger;
import com.cookingfox.lepasse.api.event.logging.EventLoggerAware;
import com.cookingfox.lepasse.api.state.State;

/**
 * Combines a multitude of logger aware interfaces.
 *
 * @param <S> The concrete type of the state object.
 */
public interface LoggerAwareCollection<S extends State> extends
        CommandLoggerAware<S>, EventLoggerAware<S> {

    /**
     * Add a logger instance that implements all supported logger interfaces.
     *
     * @param logger The logger to add.
     * @param <L>    Indicates the supported logger interfaces.
     */
    <L extends CommandLogger<S> & EventLogger<S>> void addLogger(L logger);

}
