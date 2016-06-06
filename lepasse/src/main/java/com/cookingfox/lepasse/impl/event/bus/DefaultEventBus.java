package com.cookingfox.lepasse.impl.event.bus;

import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.event.bus.EventBus;
import com.cookingfox.lepasse.api.event.exception.EventHandlerReturnedNullException;
import com.cookingfox.lepasse.api.event.handler.EventHandler;
import com.cookingfox.lepasse.api.message.Message;
import com.cookingfox.lepasse.api.message.store.MessageStore;
import com.cookingfox.lepasse.api.state.State;
import com.cookingfox.lepasse.api.state.manager.StateManager;
import com.cookingfox.lepasse.impl.message.bus.AbstractMessageBus;

import java.util.Objects;

/**
 * Default implementation of {@link EventBus}.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultEventBus<S extends State>
        extends AbstractMessageBus<Event, EventHandler<S, Event>>
        implements EventBus<S> {

    protected final StateManager<S> stateManager;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public DefaultEventBus(MessageStore messageStore, StateManager<S> stateManager) {
        super(messageStore);

        this.stateManager = Objects.requireNonNull(stateManager, "State manager can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void handleEvent(Event event) {
        handleMessage(event);
    }

    @Override
    public <E extends Event> void mapEventHandler(Class<E> eventClass, EventHandler<S, E> eventHandler) {
        // noinspection unchecked
        mapMessageHandler((Class) eventClass, (EventHandler) eventHandler);
    }

    //----------------------------------------------------------------------------------------------
    // OVERRIDDEN ABSTRACT METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void executeHandler(Event event, EventHandler<S, Event> eventHandler) {
        S currentState = stateManager.getCurrentState();
        S newState = null;

        try {
            newState = eventHandler.handle(currentState, event);
        } catch (Exception e) {
            e.printStackTrace();
            // FIXME: 06/06/16 Handle event handler exception - introduce logger & error handler
        }

        if (newState != null) {
            stateManager.handleNewState(newState, event);
            return;
        }

        throw new EventHandlerReturnedNullException(event);
    }

    @Override
    protected boolean shouldHandleMessageType(Message message) {
        return message instanceof Event;
    }

}
