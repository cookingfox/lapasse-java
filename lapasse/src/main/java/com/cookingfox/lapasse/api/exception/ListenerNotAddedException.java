package com.cookingfox.lapasse.api.exception;

/**
 * Thrown when an attempt is made to remove a listener that was not added.
 */
public class ListenerNotAddedException extends LaPasseException {

    public ListenerNotAddedException(Object listener, Object target) {
        super(String.format("Could not remove listener '%s' that was not added to '%s'",
                listener, target));
    }

}
