package com.cookingfox.lepasse.impl.logging;

import com.cookingfox.lepasse.api.command.Command;
import com.cookingfox.lepasse.api.command.logging.CommandLogger;
import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.event.logging.EventLogger;
import com.cookingfox.lepasse.api.state.State;

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
