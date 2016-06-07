package com.cookingfox.lepasse.impl.logging;

import com.cookingfox.lepasse.api.command.Command;
import com.cookingfox.lepasse.api.command.logging.CommandLogger;
import com.cookingfox.lepasse.api.event.Event;
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

    protected final Set<CommandLogger<S>> commandLoggers = new LinkedHashSet<>();
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
