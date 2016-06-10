package com.cookingfox.lapasse.api.command.handler;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import rx.Observable;

/**
 * Command handler that handles its command asynchronously.
 *
 * @param <S> The concrete type of the state object.
 * @param <C> The concrete command type that this handler will handle.
 * @param <E> The concrete event type that this handler will produce.
 */
public interface RxCommandHandler<S extends State, C extends Command, E extends Event>
        extends CommandHandler<S, C, E> {

    /**
     * Handle a command asynchronously.
     *
     * @param state   The current state object.
     * @param command The command object to handle.
     * @return The event as a result of the handled command (optional).
     */
    Observable<E> handle(S state, C command);

}
