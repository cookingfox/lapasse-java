package com.cookingfox.lapasse.impl.message.store;

import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.message.store.OnMessageAdded;
import com.cookingfox.lapasse.impl.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;

/**
 * Abstract implementation of {@link MessageStore}.
 */
public abstract class AbstractMessageStore implements MessageStore {

    /**
     * Collection of listeners for when a message is added.
     */
    protected final Set<OnMessageAdded> messageAddedListeners = CollectionUtils.newConcurrentSet();

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addMessageAddedListener(OnMessageAdded listener) {
        messageAddedListeners.add(Objects.requireNonNull(listener, "Listener can not be null"));
    }

    @Override
    public void dispose() {
        messageAddedListeners.clear();
    }

    @Override
    public void removeMessageAddedListener(OnMessageAdded listener) {
        messageAddedListeners.remove(Objects.requireNonNull(listener, "Listener can not be null"));
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Notify listeners of a newly added message.
     *
     * @param message The message that was added.
     */
    protected void notifyMessageAdded(Message message) {
        for (OnMessageAdded listener : messageAddedListeners) {
            listener.onMessageAdded(message);
        }
    }

}
