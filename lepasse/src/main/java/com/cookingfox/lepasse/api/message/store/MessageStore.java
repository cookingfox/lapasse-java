package com.cookingfox.lepasse.api.message.store;

import com.cookingfox.lepasse.api.message.Message;

/**
 * Manages stored messages.
 */
public interface MessageStore {

    /**
     * Add a new message to the store.
     *
     * @param message The message to add.
     */
    void addMessage(Message message);

    /**
     * Subscribe to when a new message is added to the store.
     *
     * @param subscriber The subscriber to notify of new messages.
     */
    void subscribe(OnMessageAdded subscriber);

    /**
     * Remove previously added subscriber.
     *
     * @param subscriber The subscriber to remove.
     */
    void unsubscribe(OnMessageAdded subscriber);

}
