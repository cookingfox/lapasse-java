package com.cookingfox.lapasse.api.command.exception;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.exception.LaPasseException;

/**
 * Thrown when there is no registered command error handler. Wraps the original exception.
 */
public class NoRegisteredCommandErrorHandlerException extends LaPasseException {

    public NoRegisteredCommandErrorHandlerException(Throwable cause, Command command) {
        super(String.format("Exception thrown by handler for command '%s'", command), cause);
    }

}
