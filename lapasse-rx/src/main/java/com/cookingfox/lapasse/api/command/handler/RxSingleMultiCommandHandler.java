package com.cookingfox.lapasse.api.command.handler;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import rx.Single;

import java.util.Collection;

/**
 * Command handler that returns an Rx Single for a collection of events.
 *
 * @param <S> The concrete type of the state object.
 * @param <C> The concrete command type that this handler will handle.
 * @param <E> The concrete event type that this handler will produce.
 */
public interface RxSingleMultiCommandHandler<S extends State, C extends Command, E extends Event>
        extends MultiCommandHandler<S, C, E> {

    /**
     * Handle a command and return an Rx Single.
     *
     * @param state   The current state object.
     * @param command The command object to handle.
     * @return An Rx Single for a collection of events.
     */
    Single<Collection<E>> handle(S state, C command);

}
