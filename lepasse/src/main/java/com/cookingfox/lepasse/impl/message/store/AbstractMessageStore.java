package com.cookingfox.lepasse.impl.message.store;

import com.cookingfox.lepasse.api.exception.NotSubscribedException;
import com.cookingfox.lepasse.api.message.Message;
import com.cookingfox.lepasse.api.message.store.MessageStore;
import com.cookingfox.lepasse.api.message.store.OnMessageAdded;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract implementation of {@link MessageStore}.
 */
public abstract class AbstractMessageStore implements MessageStore {

    /**
     * Set of unique listener instances.
     */
    protected final Set<OnMessageAdded> subscribers = new LinkedHashSet<>();

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void subscribe(OnMessageAdded subscriber) {
        subscribers.add(Objects.requireNonNull(subscriber, "Subscriber can not be null"));
    }

    @Override
    public void unsubscribe(OnMessageAdded subscriber) {
        Objects.requireNonNull(subscriber, "Subscriber can not be null");

        if (!subscribers.contains(subscriber)) {
            throw new NotSubscribedException(subscriber, this);
        }

        subscribers.remove(subscriber);
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Notify subscribed listener of a newly added message.
     *
     * @param message The message that was added.
     */
    protected void notifyMessageAdded(Message message) {
        for (OnMessageAdded subscriber : subscribers) {
            subscriber.onMessageAdded(message);
        }
    }

}
