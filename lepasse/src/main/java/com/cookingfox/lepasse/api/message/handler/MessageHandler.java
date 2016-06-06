package com.cookingfox.lepasse.api.message.handler;

import com.cookingfox.lepasse.api.message.Message;

/**
 * Marker interface for an abstract message handler.
 *
 * @param <M> The concrete message type.
 */
public interface MessageHandler<M extends Message> {
}
