package com.cookingfox.lepasse.impl.logging;

import com.cookingfox.lepasse.api.command.Command;
import com.cookingfox.lepasse.api.command.exception.NoRegisteredCommandErrorHandlerException;
import com.cookingfox.lepasse.api.command.logging.CommandLogger;
import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.event.exception.NoRegisteredEventErrorHandlerException;
import com.cookingfox.lepasse.api.event.logging.EventLogger;
import com.cookingfox.lepasse.api.logging.LoggerCollection;
import com.cookingfox.lepasse.api.state.State;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Helper class for all LePasse loggers.
 *
 * @param <S> The concrete type of the state object.
 */
public class LePasseLoggers<S extends State> implements LoggerCollection<S> {

    /**
     * Set of unique command logger instances.
     */
    protected final Set<CommandLogger<S>> commandLoggers = new LinkedHashSet<>();

    /**
     * Set of unique event logger instances.
     */
    protected final Set<EventLogger<S>> eventLoggers = new LinkedHashSet<>();

    //----------------------------------------------------------------------------------------------
    // COMMAND LOGGER
    //----------------------------------------------------------------------------------------------

    @Override
    public void addCommandLogger(CommandLogger<S> logger) {
        commandLoggers.add(Objects.requireNonNull(logger, "Logger can not be null"));
    }

    @Override
    public void onCommandHandlerError(Throwable error, Command command, Event... events) {
        if (commandLoggers.isEmpty()) {
            throw new NoRegisteredCommandErrorHandlerException(error, command);
        }

        for (CommandLogger<S> logger : commandLoggers) {
            logger.onCommandHandlerError(error, command, events);
        }
    }

    @Override
    public void onCommandHandlerResult(Command command, Event... events) {
        for (CommandLogger<S> logger : commandLoggers) {
            logger.onCommandHandlerResult(command, events);
        }
    }

    //----------------------------------------------------------------------------------------------
    // EVENT LOGGER
    //----------------------------------------------------------------------------------------------

    @Override
    public void addEventLogger(EventLogger<S> logger) {
        eventLoggers.add(Objects.requireNonNull(logger, "Logger can not be null"));
    }

    @Override
    public void onEventHandlerError(Throwable error, Event event, S newState) {
        if (eventLoggers.isEmpty()) {
            throw new NoRegisteredEventErrorHandlerException(error, event);
        }

        for (EventLogger<S> logger : eventLoggers) {
            logger.onEventHandlerError(error, event, newState);
        }
    }

    @Override
    public void onEventHandlerResult(Event event, S newState) {
        for (EventLogger<S> logger : eventLoggers) {
            logger.onEventHandlerResult(event, newState);
        }
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public <L extends CommandLogger<S> & EventLogger<S>> void addLogger(L logger) {
        addCommandLogger(logger);
        addEventLogger(logger);
    }

}
