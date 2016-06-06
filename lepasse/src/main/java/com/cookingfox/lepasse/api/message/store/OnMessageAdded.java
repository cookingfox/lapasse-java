package com.cookingfox.lepasse.api.message.store;

import com.cookingfox.lepasse.api.message.Message;

/**
 * Listener interface for when a message has been added to the message store.
 */
public interface OnMessageAdded {

    /**
     * Called when a message has been added to the message store.
     *
     * @param message The message that was added.
     */
    void onMessageAdded(Message message);

}
