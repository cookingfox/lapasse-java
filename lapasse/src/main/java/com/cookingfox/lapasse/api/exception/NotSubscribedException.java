package com.cookingfox.lapasse.api.exception;

/**
 * Thrown when an attempt is made to unsubscribe a non-subscribed subscriber.
 */
public class NotSubscribedException extends LaPasseException {

    public NotSubscribedException(Object subscriber, Object target) {
        super(String.format("Could not unsubscribe unsubscribed subscriber '%s' from '%s'",
                subscriber, target));
    }

}
