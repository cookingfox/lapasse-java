package com.cookingfox.lepasse.api.event.logging;

import com.cookingfox.lepasse.api.state.State;

/**
 * Interface for elements that can have an event logger added.
 *
 * @param <S> The concrete type of the state object.
 */
public interface EventLoggerAware<S extends State> {

    /**
     * Add an event logger.
     *
     * @param logger The event logger to add.
     */
    void addEventLogger(EventLogger<S> logger);

}
