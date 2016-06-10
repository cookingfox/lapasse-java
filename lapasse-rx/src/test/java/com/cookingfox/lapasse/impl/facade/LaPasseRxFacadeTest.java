package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.handler.RxCommandHandler;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lapasse.impl.command.bus.DefaultRxCommandBus;
import com.cookingfox.lapasse.impl.state.manager.DefaultRxStateManager;
import com.cookingfox.lapasse.impl.state.manager.DefaultStateManager;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * Unit tests for {@link LaPasseRxFacade}.
 */
public class LaPasseRxFacadeTest {

    @Test
    public void methods_should_not_throw() throws Exception {
        LaPasseRxFacade<CountState> facade = new LaPasseRxFacade.Builder<>(new CountState(0)).build();

        TestSubscriber<StateChanged<CountState>> subscriber = TestSubscriber.create();

        facade.observeStateChanges().subscribe(subscriber);

        facade.mapCommandHandler(IncrementCount.class, new RxCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Observable<CountIncremented> handle(CountState state, IncrementCount command) {
                return Observable.just(new CountIncremented(command.getCount()));
            }
        });

        facade.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return new CountState(previousState.getCount() + event.getCount());
            }
        });

        facade.handleCommand(new IncrementCount(123));

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: Builder
    //----------------------------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void setCommandBus_should_throw_if_not_rx_impl() throws Exception {
        CountState initialState = new CountState(0);
        LaPasseRxFacade<CountState> _ = new LaPasseRxFacade.Builder<>(initialState).build();
        LaPasseRxFacade.Builder<CountState> builder = new LaPasseRxFacade.Builder<>(initialState);
        builder.setCommandBus(new DefaultCommandBus<>(
                builder.createDefaultMessageStore(), _.eventBus, _.loggers, _.stateObserver
        ));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStateManager_should_throw_if_not_rx_impl() throws Exception {
        CountState initialState = new CountState(0);
        LaPasseRxFacade.Builder<CountState> builder = new LaPasseRxFacade.Builder<>(initialState);
        builder.setStateManager(new DefaultStateManager<>(initialState));
    }

    @Test
    public void setCommandBus_should_accept_rx_impl() throws Exception {
        CountState initialState = new CountState(0);
        LaPasseRxFacade<CountState> _ = new LaPasseRxFacade.Builder<>(initialState).build();
        LaPasseRxFacade.Builder<CountState> builder = new LaPasseRxFacade.Builder<>(initialState);
        DefaultRxStateManager<CountState> stateManager = new DefaultRxStateManager<>(initialState);
        builder.setCommandBus(new DefaultRxCommandBus<>(
                builder.createDefaultMessageStore(), _.eventBus, _.loggers, stateManager
        ));
    }

    @Test
    public void setStateManager_should_accept_rx_impl() throws Exception {
        CountState initialState = new CountState(0);
        LaPasseRxFacade.Builder<CountState> builder = new LaPasseRxFacade.Builder<>(initialState);
        builder.setStateManager(new DefaultRxStateManager<>(initialState));
    }

}
