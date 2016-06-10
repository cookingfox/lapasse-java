package com.cookingfox.lapasse.impl.command.bus;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.command.exception.UnsupportedCommandHandlerException;
import com.cookingfox.lapasse.api.command.handler.*;
import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.logging.LoggerCollection;
import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.StateObserver;
import com.cookingfox.lapasse.impl.message.bus.AbstractMessageBus;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Default implementation of {@link CommandBus}.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultCommandBus<S extends State>
        extends AbstractMessageBus<Command, CommandHandler<S, Command, Event>>
        implements CommandBus<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------

    /**
     * Set of supported command handlers.
     */
    protected static final Set<Class<? extends CommandHandler>> SUPPORTED = new LinkedHashSet<>();

    static {
        // add supported command handler implementations
        SUPPORTED.add(VoidCommandHandler.class);
        SUPPORTED.add(SyncCommandHandler.class);
        SUPPORTED.add(SyncMultiCommandHandler.class);
        SUPPORTED.add(AsyncCommandHandler.class);
        SUPPORTED.add(AsyncMultiCommandHandler.class);
    }

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * Executor service that runs the async command handlers.
     */
    protected ExecutorService commandHandlerExecutor;

    /**
     * The event bus to pass generated events to.
     */
    protected final EventBus<S> eventBus;

    /**
     * Used for logging the command handler operations.
     */
    protected final LoggerCollection<S> loggers;

    /**
     * Provides access to the current state.
     */
    protected final StateObserver<S> stateObserver;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public DefaultCommandBus(MessageStore messageStore,
                             EventBus<S> eventBus,
                             LoggerCollection<S> loggers,
                             StateObserver<S> stateObserver) {
        super(messageStore);

        this.eventBus = Objects.requireNonNull(eventBus, "Event bus can not be null");
        this.loggers = Objects.requireNonNull(loggers, "Loggers can not be null");
        this.stateObserver = Objects.requireNonNull(stateObserver, "State observer can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addCommandLogger(CommandLogger<S> logger) {
        loggers.addCommandLogger(logger);
    }

    @Override
    public void handleCommand(Command command) {
        handleMessage(command);
    }

    @Override
    public <C extends Command, E extends Event> void mapCommandHandler(
            Class<C> commandClass, CommandHandler<S, C, E> commandHandler) {
        isCommandHandlerSupported(Objects.requireNonNull(commandHandler, "Command handler can not be null"));

        // noinspection unchecked
        mapMessageHandler((Class) commandClass, (CommandHandler) commandHandler);
    }

    /**
     * Sets the executor service to use for executing async command handlers.
     *
     * @param executor The executor service to use.
     */
    @Override
    public void setCommandHandlerExecutor(ExecutorService executor) {
        this.commandHandlerExecutor = Objects.requireNonNull(executor,
                "Command handler executor service can not be null");
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
        Event event = null;

        try {
            if (handler instanceof VoidCommandHandler) {
                ((VoidCommandHandler<S, Command>) handler).handle(state, command);
            } else if (handler instanceof SyncCommandHandler) {
                event = ((SyncCommandHandler<S, Command, Event>) handler).handle(state, command);
            } else if (handler instanceof AsyncCommandHandler) {
                Callable<Event> callable = ((AsyncCommandHandler<S, Command, Event>) handler).handle(state, command);
                event = getCommandHandlerExecutor().submit(callable).get();
            } else {
                throw new UnsupportedCommandHandlerException(handler);
            }
        } catch (Exception e) {
            handleCommandHandlerResult(e, command, null);
            return;
        }

        handleCommandHandlerResult(null, command, event);
    }

    /**
     * Execute a command handler that produces a collection of events.
     *
     * @param state   The current state object.
     * @param command The command object.
     * @param handler The handler to execute.
     */
    protected void executeMultiCommandHandler(S state, Command command, MultiCommandHandler<S, Command, Event> handler) {
        Collection<Event> events;

        try {
            if (handler instanceof SyncMultiCommandHandler) {
                events = ((SyncMultiCommandHandler<S, Command, Event>) handler).handle(state, command);
            } else if (handler instanceof AsyncMultiCommandHandler) {
                Callable<Collection<Event>> callable = ((AsyncMultiCommandHandler<S, Command, Event>) handler).handle(state, command);
                events = getCommandHandlerExecutor().submit(callable).get();
            } else {
                throw new UnsupportedCommandHandlerException(handler);
            }
        } catch (Exception e) {
            handleMultiCommandHandlerResult(e, command, null);
            return;
        }

        handleMultiCommandHandlerResult(null, command, events);
    }

    /**
     * Returns the command handler executor. Creates a new single thread executor if it has not been
     * set explicitly.
     *
     * @return The command handler executor.
     */
    protected ExecutorService getCommandHandlerExecutor() {
        if (commandHandlerExecutor == null) {
            commandHandlerExecutor = Executors.newSingleThreadExecutor();
        }

        return commandHandlerExecutor;
    }

    protected void handleCommandHandlerResult(Throwable error, Command command, Event event) {
        if (error != null) {
            loggers.onCommandHandlerError(error, command);
            return;
        }

        loggers.onCommandHandlerResult(command, event);

        if (event != null) {
            eventBus.handleEvent(event);
        }
    }

    protected void handleMultiCommandHandlerResult(Throwable error, Command command, Collection<Event> events) {
        if (error != null) {
            loggers.onCommandHandlerError(error, command);
            return;
        }

        if (events == null) {
            loggers.onCommandHandlerResult(command);
        } else {
            loggers.onCommandHandlerResult(command, events.toArray(new Event[]{}));

            for (Event event : events) {
                eventBus.handleEvent(event);
            }
        }
    }

    /**
     * Checks whether this command handler implementation is supported.
     *
     * @param commandHandler
     * @param <C>
     * @param <E>
     */
    protected <C extends Command, E extends Event> void isCommandHandlerSupported(
            CommandHandler<S, C, E> commandHandler) {
        for (Class<? extends CommandHandler> supported : SUPPORTED) {
            if (supported.isInstance(commandHandler)) {
                return;
            }
        }

        throw new UnsupportedCommandHandlerException(commandHandler);
    }

}
