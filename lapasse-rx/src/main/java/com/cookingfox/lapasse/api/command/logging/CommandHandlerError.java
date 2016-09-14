package com.cookingfox.lapasse.api.command.logging;

import com.cookingfox.lapasse.api.command.Command;

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

}
