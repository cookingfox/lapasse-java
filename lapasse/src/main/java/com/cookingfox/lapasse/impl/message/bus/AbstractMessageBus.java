package com.cookingfox.lapasse.impl.message.bus;

import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.bus.MessageBus;
import com.cookingfox.lapasse.api.message.exception.NoMessageHandlersException;
import com.cookingfox.lapasse.api.message.handler.MessageHandler;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.message.store.OnMessageAdded;

import java.util.*;

/**
 * Abstract message bus implementation.
 *
 * @param <M> The concrete message type.
 * @param <H> The concrete message handler type.
 */
public abstract class AbstractMessageBus<M extends Message, H extends MessageHandler<M>>
        implements MessageBus<M, H> {

    //----------------------------------------------------------------------------------------------
    // PROTECTED PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * A map of message types to a set of message handlers.
     */
    protected final Map<Class<M>, Set<H>> messageHandlerMap = new LinkedHashMap<>();

    /**
     * Stores messages.
     */
    protected final MessageStore messageStore;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public AbstractMessageBus(MessageStore messageStore) {
        messageStore.subscribe(onMessageAddedToStore);

        this.messageStore = messageStore;
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void dispose() {
        messageHandlerMap.clear();
    }

    @Override
    public void handleMessage(M message) {
        Class<? extends Message> messageClass = message.getClass();

        // no mapped handlers? throw
        if (getMessageHandlers(messageClass) == null) {
            throw new NoMessageHandlersException(messageClass);
        }

        /**
         * Store the message - will call subscriber after the message is stored.
         * @see #onMessageAddedToStore
         */
        messageStore.addMessage(message);
    }

    @Override
    public void mapMessageHandler(Class<M> messageClass, H messageHandler) {
        Objects.requireNonNull(messageClass, "Message class can not be null");
        Objects.requireNonNull(messageHandler, "Message handler can not be null");

        Set<H> handlers = messageHandlerMap.get(messageClass);

        // no handler collection yet? create it first
        if (handlers == null) {
            handlers = new LinkedHashSet<>();
            messageHandlerMap.put(messageClass, handlers);
        }

        handlers.add(messageHandler);
    }

    //----------------------------------------------------------------------------------------------
    // ABSTRACT PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Actually execute the message handler for this message.
     *
     * @param message        The message to handle.
     * @param messageHandler The message handler that is associated with this message.
     */
    protected abstract void executeHandler(M message, H messageHandler);

    /**
     * Returns whether this message bus implementation should handle the concrete message object.
     * Typically this returns the result of an `instanceof` check for the concrete message type `M`.
     *
     * @param message The concrete message object.
     * @return Whether the message should be handled by this message bus.
     * @see M
     */
    protected abstract boolean shouldHandleMessageType(Message message);

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Get mapped handlers for this message class.
     *
     * @param messageClass The message class to get handlers for.
     * @return The handlers for this message class.
     * @throws NoMessageHandlersException when no handlers are mapped for this message.
     */
    protected Set<H> getMessageHandlers(Class<? extends Message> messageClass) {
        // noinspection SuspiciousMethodCalls
        Set<H> handlers = messageHandlerMap.get(messageClass);

        // no mapped handlers for this message type: see if there's a handler for the message's
        // super type
        if (handlers == null) {
            for (Class<M> messageSuperClass : messageHandlerMap.keySet()) {
                // does the message type extend this super type? if so, use its handlers
                if (messageSuperClass.isAssignableFrom(messageClass)) {
                    handlers = messageHandlerMap.get(messageSuperClass);
                    break;
                }
            }
        }

        return handlers;
    }

    /**
     * Message store subscriber.
     */
    protected final OnMessageAdded onMessageAddedToStore = new OnMessageAdded() {
        @Override
        public void onMessageAdded(Message message) {
            Set<H> handlers = getMessageHandlers(message.getClass());

            if (!shouldHandleMessageType(message) || handlers == null) {
                // this message bus should not handle messages of this type
                return;
            }

            // execute message handlers
            for (H handler : handlers) {
                // noinspection unchecked
                executeHandler((M) message, handler);
            }
        }
    };

}
