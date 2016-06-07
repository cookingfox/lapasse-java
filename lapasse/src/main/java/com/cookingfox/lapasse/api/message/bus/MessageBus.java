package com.cookingfox.lapasse.api.message.bus;

import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.handler.MessageHandler;

/**
 * Map message handlers and execute them by handling message objects.
 *
 * @param <M> The concrete message type.
 * @param <H> The concrete message handler type.
 */
public interface MessageBus<M extends Message, H extends MessageHandler<M>> {

    /**
     * Execute the message handler that is mapped to this message type. Throws if no handler is
     * mapped yet.
     *
     * @param message The message to handle.
     * @see #mapMessageHandler(Class, MessageHandler)
     */
    void handleMessage(M message);

    /**
     * Map a message handler for a concrete message type.
     *
     * @param messageClass   The concrete message type.
     * @param messageHandler The handler that is executed when {@link #handleMessage(Message)} is
     *                       called.
     */
    void mapMessageHandler(Class<M> messageClass, H messageHandler);

}
