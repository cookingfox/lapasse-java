package com.cookingfox.lapasse.impl.message.store;

import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.store.MessageStore;

import java.util.Objects;

/**
 * Implementation of {@link MessageStore} without storage mechanism. Notifies subscribers
 * immediately when a new message is added.
 */
public class NoStorageMessageStore extends AbstractMessageStore {

    @Override
    public void addMessage(Message message) {
        notifyMessageAdded(Objects.requireNonNull(message, "Message can not be null"));
    }

}
