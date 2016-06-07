package com.cookingfox.lapasse.impl.logging;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.state.State;

/**
 * Implementation of multiple logger interfaces.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultLogger<S extends State> implements CommandLogger<S>, EventLogger<S> {

    @Override
    public void onCommandHandlerError(Throwable error, Command command, Event... events) {
        // override in subclass
    }

    @Override
    public void onCommandHandlerResult(Command command, Event... events) {
        // override in subclass
    }

    @Override
    public void onEventHandlerError(Throwable error, Event event, S newState) {
        // override in subclass
    }

    @Override
    public void onEventHandlerResult(Event event, S newState) {
        // override in subclass
    }

}
