package com.cookingfox.lepasse.api.command.exception;

import com.cookingfox.lepasse.api.command.Command;
import com.cookingfox.lepasse.api.exception.LePasseException;

/**
 * Thrown when there is no registered command error handler. Wraps the original exception.
 */
public class NoRegisteredCommandErrorHandlerException extends LePasseException {

    public NoRegisteredCommandErrorHandlerException(Throwable cause, Command command) {
        super(String.format("Exception thrown by handler for command '%s'", command), cause);
    }

}
