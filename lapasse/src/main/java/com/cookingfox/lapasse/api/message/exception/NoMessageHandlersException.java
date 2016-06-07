package com.cookingfox.lapasse.api.message.exception;

import com.cookingfox.lapasse.api.exception.LaPasseException;
import com.cookingfox.lapasse.api.message.Message;

/**
 * Thrown when a message has no mapped handlers.
 */
public class NoMessageHandlersException extends LaPasseException {

    public NoMessageHandlersException(Class<? extends Message> messageClass) {
        super(String.format("No mapped handlers for message '%s'", messageClass.getName()));
    }

}
