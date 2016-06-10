package com.cookingfox.lapasse.impl.internal;

import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.command.handler.CommandHandler;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.handler.EventHandler;

/**
 * Internal helper interface that provides a hook for making command and event handler mappings.
 */
public interface HandlerMapper {

    /**
     * The implementation of this method should contain
     * {@link CommandBus#mapCommandHandler(Class, CommandHandler)} and
     * {@link EventBus#mapEventHandler(Class, EventHandler)} calls.
     */
    void mapHandlers();

}
