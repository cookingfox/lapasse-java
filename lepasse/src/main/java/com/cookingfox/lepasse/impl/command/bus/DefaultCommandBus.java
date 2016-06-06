package com.cookingfox.lepasse.impl.command.bus;

import com.cookingfox.lepasse.api.command.Command;
import com.cookingfox.lepasse.api.command.bus.CommandBus;
import com.cookingfox.lepasse.api.command.exception.UnsupportedCommandHandlerException;
import com.cookingfox.lepasse.api.command.handler.CommandHandler;
import com.cookingfox.lepasse.api.command.handler.MultiCommandHandler;
import com.cookingfox.lepasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lepasse.api.command.handler.SyncMultiCommandHandler;
import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.event.bus.EventBus;
import com.cookingfox.lepasse.api.message.Message;
import com.cookingfox.lepasse.api.message.store.MessageStore;
import com.cookingfox.lepasse.api.state.State;
import com.cookingfox.lepasse.api.state.observer.StateObserver;
import com.cookingfox.lepasse.impl.message.bus.AbstractMessageBus;

import java.util.Collection;
import java.util.Objects;

/**
 * Default implementation of {@link CommandBus}.
 *
 * @param <S>
 */
public class DefaultCommandBus<S extends State>
        extends AbstractMessageBus<Command, CommandHandler<S, Command, Event>>
        implements CommandBus<S> {

    /**
     * The event bus to pass generated events to.
     */
    protected final EventBus<S> eventBus;

    /**
     * Provides access to the current state.
     */
    protected final StateObserver<S> stateObserver;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public DefaultCommandBus(MessageStore messageStore, EventBus<S> eventBus, StateObserver<S> stateObserver) {
        super(messageStore);

        this.eventBus = Objects.requireNonNull(eventBus, "Event bus can not be null");
        this.stateObserver = Objects.requireNonNull(stateObserver, "State observer can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void handleCommand(Command command) {
        handleMessage(command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Command, E extends Event> void mapCommandHandler(Class<C> commandClass, CommandHandler<S, C, E> commandHandler) {
        mapMessageHandler((Class) commandClass, (CommandHandler) commandHandler);
    }

    //----------------------------------------------------------------------------------------------
    // OVERRIDDEN ABSTRACT METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void executeHandler(Command command, CommandHandler<S, Command, Event> commandHandler) {
        S currentState = stateObserver.getCurrentState();

        if (commandHandler instanceof MultiCommandHandler) {
            executeMultiCommandHandler(currentState, command, (MultiCommandHandler<S, Command, Event>) commandHandler);
        } else {
            executeCommandHandler(currentState, command, commandHandler);
        }
    }

    @Override
    protected boolean shouldHandleMessageType(Message message) {
        return message instanceof Command;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Execute a command handler that produces 0 or 1 event.
     *
     * @param state   The current state object.
     * @param command The command object.
     * @param handler The handler to execute.
     */
    protected void executeCommandHandler(S state, Command command, CommandHandler<S, Command, Event> handler) {
        Event event;

        if (handler instanceof SyncCommandHandler) {
            event = ((SyncCommandHandler<S, Command, Event>) handler).handle(state, command);
        } else {
            throw new UnsupportedCommandHandlerException(handler);
        }

        if (event != null) {
            eventBus.handleEvent(event);
        }
    }

    /**
     * Execute a command handler that produces 0 or more events.
     *
     * @param state   The current state object.
     * @param command The command object.
     * @param handler The handler to execute.
     */
    protected void executeMultiCommandHandler(S state, Command command, MultiCommandHandler<S, Command, Event> handler) {
        Collection<Event> events;

        if (handler instanceof SyncMultiCommandHandler) {
            events = ((SyncMultiCommandHandler<S, Command, Event>) handler).handle(state, command);
        } else {
            throw new UnsupportedCommandHandlerException(handler);
        }

        if (events != null) {
            for (Event event : events) {
                eventBus.handleEvent(event);
            }
        }
    }

}
