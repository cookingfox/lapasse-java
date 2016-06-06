package com.cookingfox.lepasse.api.command.handler;

import com.cookingfox.lepasse.api.command.Command;
import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.message.handler.MessageHandler;
import com.cookingfox.lepasse.api.state.State;

/**
 * Base interface for a command handler.
 *
 * @param <S> The concrete type of the state object.
 * @param <C> The concrete command type that this handler will handle.
 * @param <E> The concrete event type that this handler will produce.
 */
public interface CommandHandler<S extends State, C extends Command, E extends Event> extends MessageHandler<C> {
}
