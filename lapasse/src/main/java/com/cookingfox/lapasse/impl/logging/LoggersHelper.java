package com.cookingfox.lapasse.impl.logging;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.exception.NoRegisteredCommandErrorHandlerException;
import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.exception.NoRegisteredEventErrorHandlerException;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.logging.LoggerCollection;
import com.cookingfox.lapasse.api.state.State;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Helper class for all LaPasse loggers.
 *
 * @param <S> The concrete type of the state object.
 */
public class LoggersHelper<S extends State> implements LoggerCollection<S> {

    /**
     * Set of unique command logger instances.
     */
    protected final Set<CommandLogger> commandLoggers = new LinkedHashSet<>();

    /**
     * Set of unique event logger instances.
     */
    protected final Set<EventLogger<S>> eventLoggers = new LinkedHashSet<>();

    //----------------------------------------------------------------------------------------------
    // COMMAND LOGGER
    //----------------------------------------------------------------------------------------------

    @Override
    public void addCommandLogger(CommandLogger logger) {
        commandLoggers.add(Objects.requireNonNull(logger, "Logger can not be null"));
    }

    @Override
    public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
        if (commandLoggers.isEmpty()) {
            throw new NoRegisteredCommandErrorHandlerException(error, command);
        }

        for (CommandLogger logger : commandLoggers) {
            logger.onCommandHandlerError(error, command, events);
        }
    }

    @Override
    public void onCommandHandlerResult(Command command, Collection<Event> events) {
        for (CommandLogger logger : commandLoggers) {
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
    public void addLogger(CombinedLogger<S> logger) {
        addCommandLogger(logger);
        addEventLogger(logger);
    }

    @Override
    public void dispose() {
        commandLoggers.clear();
        eventLoggers.clear();
    }

}
