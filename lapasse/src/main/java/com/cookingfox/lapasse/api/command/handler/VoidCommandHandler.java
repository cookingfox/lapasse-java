package com.cookingfox.lapasse.api.command.handler;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

/**
 * Command handler that does not return anything.
 *
 * @param <S> The concrete type of the state object.
 * @param <C> The concrete command type that this handler will handle.
 * @param <E> The concrete event type that this handler will produce.
 */
public interface VoidCommandHandler<S extends State, C extends Command, E extends Event>
        extends CommandHandler<S, C, E> {

    /**
     * Handle a command synchronously.
     *
     * @param state   The current state object.
     * @param command The command object to handle.
     */
    void handle(S state, C command);

}
