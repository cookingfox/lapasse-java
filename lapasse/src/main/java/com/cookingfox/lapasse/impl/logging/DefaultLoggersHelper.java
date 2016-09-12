package com.cookingfox.lapasse.impl.logging;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.exception.NoRegisteredCommandLoggerException;
import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.exception.NoRegisteredEventLoggerException;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.exception.LoggerNotAddedException;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.logging.LoggersHelper;
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
public class DefaultLoggersHelper<S extends State> implements LoggersHelper<S> {

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
    public void removeCommandLogger(CommandLogger logger) {
        if (!commandLoggers.contains(Objects.requireNonNull(logger, "Logger can not be null"))) {
            throw new LoggerNotAddedException(logger, this);
        }

        commandLoggers.remove(logger);
    }

    @Override
    public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
        if (commandLoggers.isEmpty()) {
            throw new NoRegisteredCommandLoggerException(error, command);
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
    public void removeEventLogger(EventLogger<S> logger) {
        if (!eventLoggers.contains(Objects.requireNonNull(logger, "Logger can not be null"))) {
            throw new LoggerNotAddedException(logger, this);
        }

        eventLoggers.remove(logger);
    }

    @Override
    public void onEventHandlerError(Throwable error, Event event) {
        if (eventLoggers.isEmpty()) {
            throw new NoRegisteredEventLoggerException(error, event);
        }

        for (EventLogger<S> logger : eventLoggers) {
            logger.onEventHandlerError(error, event);
        }
    }

    @Override
    public void onEventHandlerResult(Event event, S newState) {
        for (EventLogger<S> logger : eventLoggers) {
            logger.onEventHandlerResult(event, newState);
        }
    }

    //----------------------------------------------------------------------------------------------
    // COMBINED LOGGER
    //----------------------------------------------------------------------------------------------

    @Override
    public void addLogger(CombinedLogger<S> logger) {
        addCommandLogger(logger);
        addEventLogger(logger);
    }

    @Override
    public void removeLogger(CombinedLogger<S> logger) {
        removeCommandLogger(logger);
        removeEventLogger(logger);
    }

    //----------------------------------------------------------------------------------------------
    // DISPOSABLE
    //----------------------------------------------------------------------------------------------

    @Override
    public void dispose() {
        commandLoggers.clear();
        eventLoggers.clear();
    }

}
