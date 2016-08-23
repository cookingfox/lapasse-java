package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.bus.RxCommandBus;
import com.cookingfox.lapasse.api.command.handler.RxCommandHandler;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.state.manager.RxStateManager;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lapasse.impl.command.bus.DefaultRxCommandBus;
import com.cookingfox.lapasse.impl.facade.LaPasseRxFacade.Builder;
import com.cookingfox.lapasse.impl.state.manager.DefaultRxStateManager;
import com.cookingfox.lapasse.impl.state.manager.DefaultStateManager;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Unit tests for {@link LaPasseRxFacade}.
 */
public class LaPasseRxFacadeTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: LaPasseRxFacade
    //----------------------------------------------------------------------------------------------

    @Test
    public void methods_should_not_throw() throws Exception {
        LaPasseRxFacade<CountState> facade = new Builder<>(new CountState(0)).build();

        facade.setCommandObserveScheduler(Schedulers.immediate());
        facade.setCommandSubscribeScheduler(Schedulers.immediate());

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
        Builder<CountState> builder = new Builder<>(new CountState(0));

        builder.setCommandBus(new DefaultCommandBus<>(builder.getMessageStore(),
                builder.getEventBus(), builder.getLoggersHelper(), builder.getStateManager()));
    }

    @Test
    public void setCommandBus_should_accept_rx_impl() throws Exception {
        Builder<CountState> builder = new Builder<>(new CountState(0));

        DefaultRxCommandBus<CountState> commandBus = new DefaultRxCommandBus<>(builder.getMessageStore(),
                builder.getEventBus(), builder.getLoggersHelper(), builder.getStateManager());

        builder.setCommandBus(commandBus);

        assertSame(commandBus, builder.getCommandBus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStateManager_should_throw_if_not_rx_impl() throws Exception {
        CountState initialState = new CountState(0);
        Builder<CountState> builder = new Builder<>(initialState);

        builder.setStateManager(new DefaultStateManager<>(initialState));
    }

    @Test
    public void setStateManager_should_accept_rx_impl() throws Exception {
        CountState initialState = new CountState(0);
        Builder<CountState> builder = new Builder<>(initialState);

        DefaultRxStateManager<CountState> stateManager = new DefaultRxStateManager<>(initialState);

        builder.setStateManager(stateManager);

        assertSame(stateManager, builder.getStateManager());
    }

    @Test
    public void setters_should_return_rx_typed_builder() throws Exception {
        Builder<CountState> builder = new Builder<>(new CountState(0));

        Builder<CountState> fromSetEventBus = builder.setEventBus(builder.getEventBus());
        Builder<CountState> fromSetLoggersHelper = builder.setLoggersHelper(builder.getLoggersHelper());
        Builder<CountState> fromSetMessageStore = builder.setMessageStore(builder.getMessageStore());

        assertSame(builder, fromSetEventBus);
        assertSame(builder, fromSetLoggersHelper);
        assertSame(builder, fromSetMessageStore);
    }

    @Test
    public void getters_should_create_defaults_if_null() throws Exception {
        Builder<CountState> builder = new Builder<>(new CountState(0));
        builder.commandBus = null;
        builder.stateManager = null;

        RxCommandBus<CountState> commandBus = builder.getCommandBus();
        RxStateManager<CountState> stateManager = builder.getStateManager();

        assertNotNull(commandBus);
        assertNotNull(stateManager);
    }

}
