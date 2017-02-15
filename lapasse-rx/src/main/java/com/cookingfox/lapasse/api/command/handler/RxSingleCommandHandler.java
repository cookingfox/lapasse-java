package com.cookingfox.lapasse.api.command.handler;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import rx.Single;

/**
 * Command handler that returns an Rx Single for the event it produces.
 *
 * @param <S> The concrete type of the state object.
 * @param <C> The concrete command type that this handler will handle.
 * @param <E> The concrete event type that this handler will produce.
 */
public interface RxSingleCommandHandler<S extends State, C extends Command, E extends Event>
        extends CommandHandler<S, C, E> {

    /**
     * Handle a command and return an Rx Single.
     *
     * @param state   The current state object.
     * @param command The command object to handle.
     * @return An Rx Single for the event.
     */
    Single<E> handle(S state, C command);

}
