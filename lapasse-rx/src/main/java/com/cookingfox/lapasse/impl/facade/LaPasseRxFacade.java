package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.command.bus.RxCommandBus;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.facade.RxFacade;
import com.cookingfox.lapasse.api.logging.LoggerCollection;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.RxStateManager;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.impl.command.bus.DefaultRxCommandBus;
import com.cookingfox.lapasse.impl.state.manager.DefaultRxStateManager;
import rx.Observable;

/**
 * Implementation of {@link Facade}, containing a Builder class.
 *
 * @param <S> The concrete type of the state object.
 */
public class LaPasseRxFacade<S extends State> extends LaPasseFacade<S> implements RxFacade<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public LaPasseRxFacade(CommandBus<S> commandBus,
                           EventBus<S> eventBus,
                           LoggerCollection<S> loggers,
                           RxStateObserver<S> stateObserver) {
        super(commandBus, eventBus, loggers, stateObserver);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public Observable<StateChanged<S>> observeStateChanges() {
        return getRxStateObserver().observeStateChanges();
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    protected RxStateObserver<S> getRxStateObserver() {
        return (RxStateObserver<S>) stateObserver;
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

        @Override
        public LaPasseRxFacade<S> build() {
            return (LaPasseRxFacade<S>) super.build();
        }

        //------------------------------------------------------------------------------------------
        // PROTECTED METHODS
        //------------------------------------------------------------------------------------------

        @Override
        protected CommandBus<S> createDefaultCommandBus(MessageStore messageStore,
                                                        EventBus<S> eventBus,
                                                        LoggerCollection<S> loggers,
                                                        StateManager<S> stateManager) {
            return new DefaultRxCommandBus<>(messageStore, eventBus, loggers, (RxStateManager<S>) stateManager);
        }

        @Override
        protected StateManager<S> createDefaultStateManager(S initialState) {
            return new DefaultRxStateManager<>(initialState);
        }

        @Override
        protected LaPasseRxFacade<S> createFacade(CommandBus<S> commandBus,
                                                  EventBus<S> eventBus,
                                                  LoggerCollection<S> loggers,
                                                  StateManager<S> stateManager) {
            return new LaPasseRxFacade<>(commandBus, eventBus, loggers, (RxStateManager<S>) stateManager);
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
        public LaPasseRxFacade.Builder<S> setStateManager(StateManager<S> stateManager) {
            if (!(stateManager instanceof RxStateManager)) {
                throw new IllegalArgumentException("State manager must be an implementation of " + RxStateManager.class);
            }

            return (LaPasseRxFacade.Builder<S>) super.setStateManager(stateManager);
        }

    }

}
