package com.cookingfox.lepasse.impl.facade;

import com.cookingfox.lepasse.api.command.Command;
import com.cookingfox.lepasse.api.command.bus.CommandBus;
import com.cookingfox.lepasse.api.command.handler.CommandHandler;
import com.cookingfox.lepasse.api.command.logging.CommandLogger;
import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.event.bus.EventBus;
import com.cookingfox.lepasse.api.event.handler.EventHandler;
import com.cookingfox.lepasse.api.event.logging.EventLogger;
import com.cookingfox.lepasse.api.facade.Facade;
import com.cookingfox.lepasse.api.logging.LoggerCollection;
import com.cookingfox.lepasse.api.message.store.MessageStore;
import com.cookingfox.lepasse.api.state.State;
import com.cookingfox.lepasse.api.state.manager.StateManager;
import com.cookingfox.lepasse.api.state.observer.OnStateChanged;
import com.cookingfox.lepasse.api.state.observer.StateObserver;
import com.cookingfox.lepasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lepasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lepasse.impl.logging.LePasseLoggers;
import com.cookingfox.lepasse.impl.message.store.NoStorageMessageStore;
import com.cookingfox.lepasse.impl.state.manager.DefaultStateManager;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Implementation of {@link Facade}, containing a Builder class.
 *
 * @param <S> The concrete type of the state object.
 */
public final class LePasseFacade<S extends State> implements Facade<S> {

    protected final CommandBus<S> commandBus;
    protected final EventBus<S> eventBus;
    protected final StateObserver<S> stateObserver;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public LePasseFacade(CommandBus<S> commandBus,
                         EventBus<S> eventBus,
                         StateObserver<S> stateObserver) {
        this.commandBus = Objects.requireNonNull(commandBus, "Command bus can not be null");
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus can not be null");
        this.stateObserver = Objects.requireNonNull(stateObserver, "State observer can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND BUS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addCommandLogger(CommandLogger<S> logger) {
        commandBus.addCommandLogger(logger);
    }

    @Override
    public void handleCommand(Command command) {
        commandBus.handleCommand(command);
    }

    @Override
    public <C extends Command, E extends Event> void mapCommandHandler(Class<C> commandClass, CommandHandler<S, C, E> commandHandler) {
        commandBus.mapCommandHandler(commandClass, commandHandler);
    }

    @Override
    public void setCommandHandlerExecutor(ExecutorService executor) {
        commandBus.setCommandHandlerExecutor(executor);
    }

    //----------------------------------------------------------------------------------------------
    // EVENT BUS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addEventLogger(EventLogger<S> logger) {
        eventBus.addEventLogger(logger);
    }

    @Override
    public void handleEvent(Event event) {
        eventBus.handleEvent(event);
    }

    @Override
    public <E extends Event> void mapEventHandler(Class<E> eventClass, EventHandler<S, E> eventHandler) {
        eventBus.mapEventHandler(eventClass, eventHandler);
    }

    //----------------------------------------------------------------------------------------------
    // STATE OBSERVER
    //----------------------------------------------------------------------------------------------

    @Override
    public S getCurrentState() {
        return stateObserver.getCurrentState();
    }

    @Override
    public void subscribe(OnStateChanged<S> subscriber) {
        stateObserver.subscribe(subscriber);
    }

    @Override
    public void unsubscribe(OnStateChanged<S> subscriber) {
        stateObserver.unsubscribe(subscriber);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC STATIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new facade builder.
     *
     * @param initialState The initial state object for LePasse.
     * @param <S>          The concrete type of the state object.
     * @return The facade builder.
     */
    public static <S extends State> Builder<S> builder(S initialState) {
        return new Builder<>(initialState);
    }

    //----------------------------------------------------------------------------------------------
    // INNER CLASS: BUILDER
    //----------------------------------------------------------------------------------------------

    /**
     * Facade builder class.
     *
     * @param <S> The concrete type of the state object.
     */
    public static class Builder<S extends State> {

        protected CommandBus<S> commandBus;
        protected EventBus<S> eventBus;
        protected final S initialState;
        protected LoggerCollection<S> loggers;
        protected MessageStore messageStore;
        protected StateManager<S> stateManager;

        //------------------------------------------------------------------------------------------
        // CONSTRUCTOR
        //------------------------------------------------------------------------------------------

        public Builder(S initialState) {
            this.initialState = Objects.requireNonNull(initialState, "Initial state can not be null");
        }

        //------------------------------------------------------------------------------------------
        // PUBLIC METHODS
        //------------------------------------------------------------------------------------------

        /**
         * Build a new facade using the current settings.
         *
         * @return The created facade.
         */
        public LePasseFacade<S> build() {
            CommandBus<S> _commandBus = commandBus;
            EventBus<S> _eventBus = eventBus;
            LoggerCollection<S> _loggers = loggers;
            MessageStore _messageStore = messageStore;
            StateManager<S> _stateManager = stateManager;

            if (_loggers == null) {
                _loggers = new LePasseLoggers<>();
            }

            // default to message store without storage
            if (_messageStore == null) {
                _messageStore = new NoStorageMessageStore();
            }

            // pass initial state to state manager
            if (_stateManager == null) {
                _stateManager = new DefaultStateManager<>(initialState);
            }

            if (_eventBus == null) {
                _eventBus = new DefaultEventBus<>(_messageStore, _loggers, _stateManager);
            }

            if (_commandBus == null) {
                _commandBus = new DefaultCommandBus<>(_messageStore, _eventBus, _loggers, _stateManager);
            }

            return new LePasseFacade<>(_commandBus, _eventBus, _stateManager);
        }

        //------------------------------------------------------------------------------------------
        // SETTERS
        //------------------------------------------------------------------------------------------

        public Builder<S> setCommandBus(CommandBus<S> commandBus) {
            this.commandBus = Objects.requireNonNull(commandBus, "Command bus can not be null");
            return this;
        }

        public Builder<S> setEventBus(EventBus<S> eventBus) {
            this.eventBus = Objects.requireNonNull(eventBus, "Event bus can not be null");
            return this;
        }

        public Builder<S> setLoggers(LoggerCollection<S> loggers) {
            this.loggers = Objects.requireNonNull(loggers, "Loggers can not be null");
            return this;
        }

        public Builder<S> setMessageStore(MessageStore messageStore) {
            this.messageStore = Objects.requireNonNull(messageStore, "Message store can not be null");
            return this;
        }

        public Builder<S> setStateManager(StateManager<S> stateManager) {
            this.stateManager = Objects.requireNonNull(stateManager, "State manager can not be null");
            return this;
        }

    }

}
