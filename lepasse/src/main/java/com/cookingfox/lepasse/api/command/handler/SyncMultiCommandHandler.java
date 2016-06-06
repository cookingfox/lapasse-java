package com.cookingfox.lepasse.api.command.handler;

import com.cookingfox.lepasse.api.command.Command;
import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.state.State;

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
     * Handle a command synchronously.
     *
     * @param state   The current state object.
     * @param command The command object to handle.
     * @return The event as a result of the handled command (optional).
     */
    Collection<E> handle(S state, C command);

}
