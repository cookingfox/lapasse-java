package com.cookingfox.lapasse.api.message.handler;

import com.cookingfox.lapasse.api.message.Message;

/**
 * Marker interface for an abstract message handler.
 *
 * @param <M> The concrete message type.
 */
public interface MessageHandler<M extends Message> {
}
