package com.cookingfox.lapasse.api.command.handler;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import rx.Observable;

/**
 * Command handler that returns an Rx Observable for the event it produces.
 *
 * @param <S> The concrete type of the state object.
 * @param <C> The concrete command type that this handler will handle.
 * @param <E> The concrete event type that this handler will produce.
 */
public interface RxCommandHandler<S extends State, C extends Command, E extends Event>
        extends CommandHandler<S, C, E> {

    /**
     * Handle a command and return an Rx Observable.
     *
     * @param state   The current state object.
     * @param command The command object to handle.
     * @return An Rx Observable for the event.
     */
    Observable<E> handle(S state, C command);

}
