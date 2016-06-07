package com.cookingfox.lapasse.api.command.logging;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

/**
 * Contains methods for logging command handling.
 *
 * @param <S> The concrete type of the state object.
 */
public interface CommandLogger<S extends State> {

    /**
     * Called when an error occurs during the handling of a command.
     *
     * @param error   The error that occurred.
     * @param command The command that was handled.
     * @param events  The resulting event(s). Note: command handlers are not required to produce
     *                events.
     */
    void onCommandHandlerError(Throwable error, Command command, Event... events);

    /**
     * Called when a command handler returns a result.
     *
     * @param command The command that was handled.
     * @param events  The resulting event(s). Note: command handlers are not required to produce
     *                events.
     */
    void onCommandHandlerResult(Command command, Event... events);

}
