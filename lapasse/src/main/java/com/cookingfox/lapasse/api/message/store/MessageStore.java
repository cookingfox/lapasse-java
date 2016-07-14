package com.cookingfox.lapasse.api.message.store;

import com.cookingfox.lapasse.api.lifecycle.Disposable;
import com.cookingfox.lapasse.api.message.Message;

/**
 * Manages stored messages.
 */
public interface MessageStore extends Disposable {

    /**
     * Add a new message to the store.
     *
     * @param message The message to add.
     */
    void addMessage(Message message);

    /**
     * Add listener for when a message is added to the store.
     *
     * @param listener The listener to notify of newly added messages.
     */
    void addMessageAddedListener(OnMessageAdded listener);

    /**
     * Remove previously added listener.
     *
     * @param listener The listener to remove.
     */
    void removeMessageAddedListener(OnMessageAdded listener);

}
