package com.cookingfox.lapasse.impl.event.bus;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.exception.EventHandlerReturnedNullException;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.logging.LoggerCollection;
import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.impl.message.bus.AbstractMessageBus;

import java.util.Objects;

/**
 * Default implementation of {@link EventBus}.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultEventBus<S extends State>
        extends AbstractMessageBus<Event, EventHandler<S, Event>>
        implements EventBus<S> {

    /**
     * Used for logging the event handler operations.
     */
    protected final LoggerCollection<S> loggers;

    /**
     * Provides access to the current state.
     */
    protected final StateManager<S> stateManager;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public DefaultEventBus(MessageStore messageStore, LoggerCollection<S> loggers, StateManager<S> stateManager) {
        super(messageStore);

        this.loggers = Objects.requireNonNull(loggers, "Loggers can not be null");
        this.stateManager = Objects.requireNonNull(stateManager, "State manager can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addEventLogger(EventLogger<S> logger) {
        loggers.addEventLogger(logger);
    }

    @Override
    public void handleEvent(Event event) {
        handleMessage(event);
    }

    @Override
    public <E extends Event> void mapEventHandler(Class<E> eventClass, EventHandler<S, E> eventHandler) {
        // noinspection unchecked
        mapMessageHandler((Class) eventClass, (EventHandler) eventHandler);
    }

    @Override
    public void removeEventLogger(EventLogger<S> logger) {
        loggers.removeEventLogger(logger);
    }

    //----------------------------------------------------------------------------------------------
    // OVERRIDDEN ABSTRACT METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void executeHandler(Event event, EventHandler<S, Event> eventHandler) {
        S newState;

        try {
            // attempt to create a new state by applying the event to the current state
            newState = eventHandler.handle(stateManager.getCurrentState(), event);
        } catch (Exception e) {
            loggers.onEventHandlerError(e, event, null);
            return;
        }

        // log handler result
        loggers.onEventHandlerResult(event, newState);

        if (newState == null) {
            // handler returned null: log error
            loggers.onEventHandlerError(new EventHandlerReturnedNullException(event), event, null);
        } else {
            // handler returned new state: pass to state manager
            stateManager.handleNewState(newState, event);
        }
    }

    @Override
    protected boolean shouldHandleMessageType(Message message) {
        return message instanceof Event;
    }

}
