package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.command.handler.CommandHandler;
import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.logging.LoggerCollection;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.StateObserver;
import com.cookingfox.lapasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lapasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lapasse.impl.logging.LaPasseLoggers;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import com.cookingfox.lapasse.impl.state.manager.DefaultStateManager;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Implementation of {@link Facade}, containing a Builder class.
 *
 * @param <S> The concrete type of the state object.
 */
public class LaPasseFacade<S extends State> implements Facade<S> {

    protected final CommandBus<S> commandBus;
    protected final EventBus<S> eventBus;
    protected final LoggerCollection<S> loggers;
    protected final StateObserver<S> stateObserver;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public LaPasseFacade(CommandBus<S> commandBus,
                         EventBus<S> eventBus,
                         LoggerCollection<S> loggers,
                         StateObserver<S> stateObserver) {
        this.commandBus = Objects.requireNonNull(commandBus, "Command bus can not be null");
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus can not be null");
        this.loggers = Objects.requireNonNull(loggers, "Loggers can not be null");
        this.stateObserver = Objects.requireNonNull(stateObserver, "State observer can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // COMBINED LOGGER AWARE
    //----------------------------------------------------------------------------------------------

    @Override
    public void addLogger(CombinedLogger<S> logger) {
        loggers.addLogger(logger);
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND BUS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addCommandLogger(CommandLogger logger) {
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
        public LaPasseFacade<S> build() {
            CommandBus<S> _commandBus = commandBus;
            EventBus<S> _eventBus = eventBus;
            LoggerCollection<S> _loggers = loggers;
            MessageStore _messageStore = messageStore;
            StateManager<S> _stateManager = stateManager;

            if (_loggers == null) {
                _loggers = createDefaultLoggers();
            }

            // default to message store without storage
            if (_messageStore == null) {
                _messageStore = createDefaultMessageStore();
            }

            // pass initial state to state manager
            if (_stateManager == null) {
                _stateManager = createDefaultStateManager(initialState);
            }

            if (_eventBus == null) {
                _eventBus = createDefaultEventBus(_messageStore, _loggers, _stateManager);
            }

            if (_commandBus == null) {
                _commandBus = createDefaultCommandBus(_messageStore, _eventBus, _loggers, _stateManager);
            }

            return createFacade(_commandBus, _eventBus, _loggers, _stateManager);
        }

        //------------------------------------------------------------------------------------------
        // PROTECTED METHODS
        //------------------------------------------------------------------------------------------

        protected CommandBus<S> createDefaultCommandBus(MessageStore messageStore,
                                                        EventBus<S> eventBus,
                                                        LoggerCollection<S> loggers,
                                                        StateManager<S> stateManager) {
            return new DefaultCommandBus<>(messageStore, eventBus, loggers, stateManager);
        }

        protected EventBus<S> createDefaultEventBus(MessageStore messageStore,
                                                    LoggerCollection<S> loggers,
                                                    StateManager<S> stateManager) {
            return new DefaultEventBus<>(messageStore, loggers, stateManager);
        }

        protected LoggerCollection<S> createDefaultLoggers() {
            return new LaPasseLoggers<>();
        }

        protected MessageStore createDefaultMessageStore() {
            return new NoStorageMessageStore();
        }

        protected StateManager<S> createDefaultStateManager(S initialState) {
            return new DefaultStateManager<>(initialState);
        }

        protected LaPasseFacade<S> createFacade(CommandBus<S> commandBus,
                                                EventBus<S> eventBus,
                                                LoggerCollection<S> loggers,
                                                StateManager<S> stateManager) {
            return new LaPasseFacade<>(commandBus, eventBus, loggers, stateManager);
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
