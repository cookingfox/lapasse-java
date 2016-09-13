package com.cookingfox.lapasse.api.command.logging;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;

import java.util.Collection;

/**
 * Wraps a command handler error.
 */
public interface CommandHandlerError {

    /**
     * @return The command that was handled.
     */
    Command getCommand();

    /**
     * @return The error that occurred.
     */
    Throwable getError();

    /**
     * @return The resulting event(s). Note: command handlers are not required to produce events.
     */
    Collection<Event> getEvents();

}
