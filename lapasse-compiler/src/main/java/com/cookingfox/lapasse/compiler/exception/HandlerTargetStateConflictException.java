package com.cookingfox.lapasse.compiler.exception;

/**
 * Thrown when there is a handler mapping conflict, regarding the state.
 */
public class HandlerTargetStateConflictException extends Exception {

    public HandlerTargetStateConflictException(String message) {
        super(message);
    }

}
