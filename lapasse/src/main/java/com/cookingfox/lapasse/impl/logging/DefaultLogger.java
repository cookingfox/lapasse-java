package com.cookingfox.lapasse.impl.logging;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.state.State;

import java.util.Collection;

/**
 * Implementation of multiple logger interfaces.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultLogger<S extends State> implements CombinedLogger<S> {

    @Override
    public void onCommandHandlerError(Throwable error, Command command) {
        // override in subclass
    }

    @Override
    public void onCommandHandlerResult(Command command, Collection<Event> events) {
        // override in subclass
    }

    @Override
    public void onEventHandlerError(Throwable error, Event event) {
        // override in subclass
    }

    @Override
    public void onEventHandlerResult(Event event, S newState) {
        // override in subclass
    }

}
