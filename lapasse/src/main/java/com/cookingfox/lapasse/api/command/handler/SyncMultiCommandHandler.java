package com.cookingfox.lapasse.api.command.handler;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

import java.util.Collection;

/**
 * Command handler that handles its command synchronously and produces multiple events.
 *
 * @param <S> The concrete type of the state object.
 * @param <C> The concrete command type that this handler will handle.
 * @param <E> The concrete event type that this handler will produce.
 */
public interface SyncMultiCommandHandler<S extends State, C extends Command, E extends Event>
        extends MultiCommandHandler<S, C, E> {

    /**
     * Handle a command synchronously and produce multiple events.
     *
     * @param state   The current state object.
     * @param command The command object to handle.
     * @return A collection of events as a result of the handled command (optional).
     */
    Collection<E> handle(S state, C command);

}
