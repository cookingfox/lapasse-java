package com.cookingfox.lapasse.api.facade;

import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.logging.LoggersHelper;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.StateManager;

/**
 * Interface for a {@link Facade} builder.
 *
 * @param <S> The concrete type of the state object.
 */
public interface FacadeBuilder<S extends State> {

    /**
     * Build a new facade using the provided dependencies and configurations.
     *
     * @return The created facade instance.
     */
    Facade<S> build();

    /**
     * @return The command bus.
     */
    CommandBus<S> getCommandBus();

    /**
     * @return The event bus.
     */
    EventBus<S> getEventBus();

    /**
     * @return The loggers helper.
     */
    LoggersHelper<S> getLoggersHelper();

    /**
     * @return The message store.
     */
    MessageStore getMessageStore();

    /**
     * @return The state manager.
     */
    StateManager<S> getStateManager();

    /**
     * Set the instance to use with the facade.
     *
     * @param commandBus The instance to use with the facade.
     * @return The current builder instance, so that operations can be chained.
     */
    FacadeBuilder<S> setCommandBus(CommandBus<S> commandBus);

    /**
     * Set the instance to use with the facade.
     *
     * @param eventBus The instance to use with the facade.
     * @return The current builder instance, so that operations can be chained.
     */
    FacadeBuilder<S> setEventBus(EventBus<S> eventBus);

    /**
     * Set the instance to use with the facade.
     *
     * @param loggersHelper The instance to use with the facade.
     * @return The current builder instance, so that operations can be chained.
     */
    FacadeBuilder<S> setLoggersHelper(LoggersHelper<S> loggersHelper);

    /**
     * Set the instance to use with the facade.
     *
     * @param messageStore The instance to use with the facade.
     * @return The current builder instance, so that operations can be chained.
     */
    FacadeBuilder<S> setMessageStore(MessageStore messageStore);

    /**
     * Set the instance to use with the facade.
     *
     * @param stateManager The instance to use with the facade.
     * @return The current builder instance, so that operations can be chained.
     */
    FacadeBuilder<S> setStateManager(StateManager<S> stateManager);

}
