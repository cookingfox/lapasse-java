package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.command.bus.RxCommandBus;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.facade.RxFacade;
import com.cookingfox.lapasse.api.logging.LoggersHelper;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.RxStateManager;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.impl.command.bus.DefaultRxCommandBus;
import com.cookingfox.lapasse.impl.state.manager.DefaultRxStateManager;
import rx.Observable;
import rx.Scheduler;

/**
 * Implementation of {@link RxFacade}, containing a Builder class.
 *
 * @param <S> The concrete type of the state object.
 */
public class LaPasseRxFacade<S extends State> extends LaPasseFacade<S> implements RxFacade<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public LaPasseRxFacade(RxCommandBus<S> commandBus,
                           EventBus<S> eventBus,
                           LoggersHelper<S> loggers,
                           MessageStore messageStore,
                           RxStateManager<S> stateManager) {
        super(commandBus, eventBus, loggers, messageStore, stateManager);
    }

    //----------------------------------------------------------------------------------------------
    // RX COMMAND BUS
    //----------------------------------------------------------------------------------------------

    @Override
    public void setCommandObserveScheduler(Scheduler observeOnScheduler) {
        getRxCommandBus().setCommandObserveScheduler(observeOnScheduler);
    }

    @Override
    public void setCommandSubscribeScheduler(Scheduler subscribeOnScheduler) {
        getRxCommandBus().setCommandSubscribeScheduler(subscribeOnScheduler);
    }

    //----------------------------------------------------------------------------------------------
    // RX STATE OBSERVER
    //----------------------------------------------------------------------------------------------

    @Override
    public Observable<StateChanged<S>> observeStateChanges() {
        return getRxStateObserver().observeStateChanges();
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @return The command bus as Rx command bus.
     */
    protected RxCommandBus<S> getRxCommandBus() {
        return (RxCommandBus<S>) commandBus;
    }

    /**
     * @return The state observer as Rx state observer.
     */
    protected RxStateObserver<S> getRxStateObserver() {
        // noinspection unchecked
        return (RxStateObserver<S>) stateManager;
    }

    //----------------------------------------------------------------------------------------------
    // INNER CLASS: BUILDER
    //----------------------------------------------------------------------------------------------

    public static class Builder<S extends State> extends LaPasseFacade.Builder<S> {

        //------------------------------------------------------------------------------------------
        // CONSTRUCTOR
        //------------------------------------------------------------------------------------------

        public Builder(S initialState) {
            super(initialState);
        }

        //------------------------------------------------------------------------------------------
        // PUBLIC METHODS
        //------------------------------------------------------------------------------------------

        @Override
        public LaPasseRxFacade<S> build() {
            return new LaPasseRxFacade<>(getCommandBus(), getEventBus(), getLoggersHelper(),
                    getMessageStore(), getStateManager());
        }

        //------------------------------------------------------------------------------------------
        // GETTERS
        //------------------------------------------------------------------------------------------

        @Override
        public RxCommandBus<S> getCommandBus() {
            if (commandBus == null) {
                commandBus = new DefaultRxCommandBus<>(getMessageStore(), getEventBus(),
                        getLoggersHelper(), getStateManager());
            }

            return (RxCommandBus<S>) super.getCommandBus();
        }

        @Override
        public RxStateManager<S> getStateManager() {
            if (stateManager == null) {
                stateManager = new DefaultRxStateManager<>(initialState);
            }

            return (RxStateManager<S>) super.getStateManager();
        }

        //------------------------------------------------------------------------------------------
        // SETTERS
        //------------------------------------------------------------------------------------------

        @Override
        public LaPasseRxFacade.Builder<S> setCommandBus(CommandBus<S> commandBus) {
            if (!(commandBus instanceof RxCommandBus)) {
                throw new IllegalArgumentException("Command bus must be an implementation of " + RxCommandBus.class);
            }

            return (LaPasseRxFacade.Builder<S>) super.setCommandBus(commandBus);
        }

        @Override
        public LaPasseRxFacade.Builder<S> setEventBus(EventBus<S> eventBus) {
            return (LaPasseRxFacade.Builder<S>) super.setEventBus(eventBus);
        }

        @Override
        public LaPasseRxFacade.Builder<S> setLoggersHelper(LoggersHelper<S> loggersHelper) {
            return (LaPasseRxFacade.Builder<S>) super.setLoggersHelper(loggersHelper);
        }

        @Override
        public LaPasseRxFacade.Builder<S> setMessageStore(MessageStore messageStore) {
            return (LaPasseRxFacade.Builder<S>) super.setMessageStore(messageStore);
        }

        @Override
        public LaPasseRxFacade.Builder<S> setStateManager(StateManager<S> stateManager) {
            if (!(stateManager instanceof RxStateManager)) {
                throw new IllegalArgumentException("State manager must be an implementation of " + RxStateManager.class);
            }

            return (LaPasseRxFacade.Builder<S>) super.setStateManager(stateManager);
        }

    }

}
