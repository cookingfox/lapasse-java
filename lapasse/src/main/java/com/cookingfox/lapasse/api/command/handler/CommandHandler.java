package com.cookingfox.lapasse.api.command.handler;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.message.handler.MessageHandler;
import com.cookingfox.lapasse.api.state.State;

/**
 * Base interface for a command handler.
 *
 * @param <S> The concrete type of the state object.
 * @param <C> The concrete command type that this handler will handle.
 * @param <E> The concrete event type that this handler will produce.
 */
public interface CommandHandler<S extends State, C extends Command, E extends Event>
        extends MessageHandler<C> {
}
