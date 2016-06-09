package com.cookingfox.lapasse.api.logging;

import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.state.State;

/**
 * Combines multiple loggers into one interface.
 *
 * @param <S> The concrete type of the state object.
 */
public interface CombinedLogger<S extends State> extends CommandLogger<S>, EventLogger<S> {
}
