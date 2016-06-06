package com.cookingfox.lepasse.api.command.exception;

import com.cookingfox.lepasse.api.command.handler.CommandHandler;
import com.cookingfox.lepasse.api.exception.LePasseException;

/**
 * Thrown when the command handler implementation is unsupported.
 */
public class UnsupportedCommandHandlerException extends LePasseException {

    public UnsupportedCommandHandlerException(CommandHandler commandHandler) {
        super(String.format("Unsupported command handler implementation: '%s'", commandHandler));
    }

}
