package com.cookingfox.lapasse.api.event.bus;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.event.logging.EventLoggerAware;
import com.cookingfox.lapasse.api.lifecycle.Disposable;
import com.cookingfox.lapasse.api.state.State;

/**
 * Map event handlers and execute them by handling event objects.
 *
 * @param <S> The concrete type of the state object.
 */
public interface EventBus<S extends State> extends Disposable, EventLoggerAware<S> {

    /**
     * Execute the event handler that is mapped to this event type. Throws if no handler is
     * mapped yet.
     *
     * @param event The event to handle.
     * @see #mapEventHandler(Class, EventHandler)
     */
    void handleEvent(Event event);

    /**
     * Map a event handler for a concrete event type.
     *
     * @param eventClass   The concrete event type that this handler will handle.
     * @param eventHandler The handler that is executed when {@link #handleEvent(Event)} is
     *                     called.
     * @param <E>          The concrete event type that this handler will produce.
     */
    <E extends Event> void mapEventHandler(Class<E> eventClass, EventHandler<S, E> eventHandler);

}
