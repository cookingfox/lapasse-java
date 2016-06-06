package com.cookingfox.lepasse.api.message.exception;

import com.cookingfox.lepasse.api.exception.LePasseException;
import com.cookingfox.lepasse.api.message.Message;

/**
 * Thrown when a message has no mapped handlers.
 */
public class NoMessageHandlersException extends LePasseException {

    public NoMessageHandlersException(Class<? extends Message> messageClass) {
        super(String.format("No mapped handlers for message '%s'", messageClass.getName()));
    }

}
