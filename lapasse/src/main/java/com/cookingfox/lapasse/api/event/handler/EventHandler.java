package com.cookingfox.lapasse.api.event.handler;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.message.handler.MessageHandler;
import com.cookingfox.lapasse.api.state.State;

/**
 * Interface for an event handler.
 *
 * @param <S> The concrete type of the state object.
 * @param <E> The concrete event type that this handler will handle.
 */
public interface EventHandler<S extends State, E extends Event> extends MessageHandler<E> {

    /**
     * Returns the previous state or creates a new state object by applying the event to the
     * previous state.
     *
     * @param event         The event to apply.
     * @param previousState The previous state.
     * @return The previous state, or a new state with the event applied.
     */
    S handle(S previousState, E event);

}
