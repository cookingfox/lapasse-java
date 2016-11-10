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
import com.cookingfox.lapasse.api.facade.FacadeBuilder;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.logging.LoggersHelper;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lapasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lapasse.impl.logging.DefaultLoggersHelper;
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
    protected final LoggersHelper<S> loggersHelper;
    protected final MessageStore messageStore;
    protected final StateManager<S> stateManager;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public LaPasseFacade(CommandBus<S> commandBus,
                         EventBus<S> eventBus,
                         LoggersHelper<S> loggersHelper,
                         MessageStore messageStore,
                         StateManager<S> stateManager) {
        this.commandBus = Objects.requireNonNull(commandBus, "Command bus can not be null");
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus can not be null");
        this.loggersHelper = Objects.requireNonNull(loggersHelper, "Loggers helper can not be null");
        this.messageStore = Objects.requireNonNull(messageStore, "Message store can not be null");
        this.stateManager = Objects.requireNonNull(stateManager, "State manager can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // COMBINED LOGGER AWARE
    //----------------------------------------------------------------------------------------------

    @Override
    public void addLogger(CombinedLogger<S> logger) {
        loggersHelper.addLogger(logger);
    }

    @Override
    public void removeLogger(CombinedLogger<S> logger) {
        loggersHelper.removeLogger(logger);
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
    public void removeCommandLogger(CommandLogger logger) {
        commandBus.removeCommandLogger(logger);
    }

    @Override
    public void setCommandHandlerExecutor(ExecutorService executor) {
        commandBus.setCommandHandlerExecutor(executor);
    }

    //----------------------------------------------------------------------------------------------
    // DISPOSABLE
    //----------------------------------------------------------------------------------------------

    @Override
    public void dispose() {
        commandBus.dispose();
        eventBus.dispose();
        loggersHelper.dispose();
        messageStore.dispose();
        stateManager.dispose();
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

    @Override
    public void removeEventLogger(EventLogger<S> logger) {
        eventBus.removeEventLogger(logger);
    }

    //----------------------------------------------------------------------------------------------
    // STATE OBSERVER
    //----------------------------------------------------------------------------------------------

    @Override
    public void addStateChangedListener(OnStateChanged<S> listener) {
        stateManager.addStateChangedListener(listener);
    }

    @Override
    public S getCurrentState() {
        return stateManager.getCurrentState();
    }

    @Override
    public void removeStateChangedListener(OnStateChanged<S> listener) {
        stateManager.removeStateChangedListener(listener);
    }

    //----------------------------------------------------------------------------------------------
    // INNER CLASS: BUILDER
    //----------------------------------------------------------------------------------------------

    /**
     * Facade builder class.
     *
     * @param <S> The concrete type of the state object.
     */
    public static class Builder<S extends State> implements FacadeBuilder<S> {

        protected CommandBus<S> commandBus;
        protected EventBus<S> eventBus;
        protected final S initialState;
        protected LoggersHelper<S> loggersHelper;
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

        @Override
        public LaPasseFacade<S> build() {
            return new LaPasseFacade<>(getCommandBus(), getEventBus(), getLoggersHelper(),
                    getMessageStore(), getStateManager());
        }

        //------------------------------------------------------------------------------------------
        // GETTERS
        //------------------------------------------------------------------------------------------

        @Override
        public CommandBus<S> getCommandBus() {
            if (commandBus == null) {
                commandBus = new DefaultCommandBus<>(getMessageStore(), getEventBus(),
                        getLoggersHelper(), getStateManager());
            }

            return commandBus;
        }

        @Override
        public EventBus<S> getEventBus() {
            if (eventBus == null) {
                eventBus = new DefaultEventBus<>(getMessageStore(), getLoggersHelper(),
                        getStateManager());
            }

            return eventBus;
        }

        @Override
        public LoggersHelper<S> getLoggersHelper() {
            if (loggersHelper == null) {
                loggersHelper = new DefaultLoggersHelper<>();
            }

            return loggersHelper;
        }

        @Override
        public MessageStore getMessageStore() {
            if (messageStore == null) {
                messageStore = new NoStorageMessageStore();
            }

            return messageStore;
        }

        @Override
        public StateManager<S> getStateManager() {
            if (stateManager == null) {
                stateManager = new DefaultStateManager<>(initialState);
            }

            return stateManager;
        }

        //------------------------------------------------------------------------------------------
        // SETTERS
        //------------------------------------------------------------------------------------------

        @Override
        public Builder<S> setCommandBus(CommandBus<S> commandBus) {
            this.commandBus = Objects.requireNonNull(commandBus, "Command bus can not be null");
            return this;
        }

        @Override
        public Builder<S> setEventBus(EventBus<S> eventBus) {
            this.eventBus = Objects.requireNonNull(eventBus, "Event bus can not be null");
            return this;
        }

        @Override
        public Builder<S> setLoggersHelper(LoggersHelper<S> loggersHelper) {
            this.loggersHelper = Objects.requireNonNull(loggersHelper, "Loggers helper can not be null");
            return this;
        }

        @Override
        public Builder<S> setMessageStore(MessageStore messageStore) {
            this.messageStore = Objects.requireNonNull(messageStore, "Message store can not be null");
            return this;
        }

        @Override
        public Builder<S> setStateManager(StateManager<S> stateManager) {
            this.stateManager = Objects.requireNonNull(stateManager, "State manager can not be null");
            return this;
        }

    }

}
