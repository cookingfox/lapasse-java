package com.cookingfox.lapasse.api.command.exception;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.exception.LaPasseException;

/**
 * Thrown when there is no registered command logger. Wraps the original exception.
 */
public class NoRegisteredCommandLoggerException extends LaPasseException {

    public NoRegisteredCommandLoggerException(Throwable cause, Command command) {
        super(String.format("Exception thrown by handler for command '%s'", command), cause);
    }

}
