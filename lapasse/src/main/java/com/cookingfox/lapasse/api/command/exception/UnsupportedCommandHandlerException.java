package com.cookingfox.lapasse.api.command.exception;

import com.cookingfox.lapasse.api.command.handler.CommandHandler;
import com.cookingfox.lapasse.api.exception.LaPasseException;

/**
 * Thrown when the command handler implementation is unsupported.
 */
public class UnsupportedCommandHandlerException extends LaPasseException {

    public UnsupportedCommandHandlerException(CommandHandler commandHandler) {
        super(String.format("Unsupported command handler implementation: '%s'", commandHandler));
    }

}
