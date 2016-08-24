package com.cookingfox.lapasse.impl.command.bus;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.handler.RxCommandHandler;
import com.cookingfox.lapasse.api.command.handler.RxMultiCommandHandler;
import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.command.handler.SyncMultiCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.logging.LoggersHelper;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.manager.RxStateManager;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lapasse.impl.logging.DefaultLogger;
import com.cookingfox.lapasse.impl.logging.DefaultLoggersHelper;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import com.cookingfox.lapasse.impl.state.manager.DefaultRxStateManager;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DefaultRxCommandBus}.
 */
public class DefaultRxCommandBusTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private DefaultRxCommandBus<CountState> commandBus;
    private EventBus<CountState> eventBus;
    private LoggersHelper<CountState> loggers;
    private RxStateManager<CountState> stateManager;

    @Before
    public void setUp() throws Exception {
        loggers = new DefaultLoggersHelper<>();
        MessageStore messageStore = new NoStorageMessageStore();
        stateManager = new DefaultRxStateManager<>(new CountState(0));
        eventBus = new DefaultEventBus<>(messageStore, loggers, stateManager);

        commandBus = new DefaultRxCommandBus<>(messageStore, eventBus, loggers, stateManager);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: dispose
    //----------------------------------------------------------------------------------------------

    @Test
    public void dispose_should_unsubscribe_subscriptions() throws Exception {
        assertFalse(commandBus.subscriptions.isUnsubscribed());

        commandBus.dispose();

        assertTrue(commandBus.subscriptions.isUnsubscribed());
    }

    @Test
    public void dispose_should_call_super_dispose() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        assertFalse(executor.isShutdown());

        commandBus.setCommandHandlerExecutor(executor);
        commandBus.dispose();

        assertTrue(executor.isShutdown());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: executeHandler
    //----------------------------------------------------------------------------------------------

    @Test
    public void executeHandler_should_support_rx_handler() throws Exception {
        mapCountIncrementedEventHandler();

        TestSubscriber<StateChanged<CountState>> subscriber = TestSubscriber.create();

        stateManager.observeStateChanges().subscribe(subscriber);

        commandBus.mapCommandHandler(IncrementCount.class, new RxCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Observable<CountIncremented> handle(CountState state, IncrementCount command) {
                return Observable.just(new CountIncremented(command.getCount()));
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
    }

    @Test
    public void executeHandler_should_support_non_rx_handler() throws Exception {
        mapCountIncrementedEventHandler();

        TestSubscriber<StateChanged<CountState>> subscriber = TestSubscriber.create();

        stateManager.observeStateChanges().subscribe(subscriber);

        commandBus.mapCommandHandler(IncrementCount.class, new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public CountIncremented handle(CountState state, IncrementCount command) {
                return new CountIncremented(command.getCount());
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
    }

    @Test
    public void executeHandler_should_log_rx_handler_exception() throws Exception {
        mapCountIncrementedEventHandler();

        final AtomicReference<Throwable> calledError = new AtomicReference<>();

        loggers.addLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
                calledError.set(error);
            }
        });

        final RuntimeException exception = new RuntimeException("Example error");

        commandBus.mapCommandHandler(IncrementCount.class, new RxCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Observable<CountIncremented> handle(CountState state, IncrementCount command) {
                throw exception;
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        assertSame(exception, calledError.get());
    }

    @Test
    public void executeHandler_should_log_rx_handler_error() throws Exception {
        mapCountIncrementedEventHandler();

        final AtomicReference<Throwable> calledError = new AtomicReference<>();

        loggers.addLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
                calledError.set(error);
            }
        });

        final RuntimeException exception = new RuntimeException("Example error");

        commandBus.mapCommandHandler(IncrementCount.class, new RxCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Observable<CountIncremented> handle(CountState state, IncrementCount command) {
                return Observable.error(exception);
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        assertSame(exception, calledError.get());
    }

    @Test
    public void executeHandler_should_support_rx_multi_handler() throws Exception {
        mapCountIncrementedEventHandler();

        TestSubscriber<StateChanged<CountState>> subscriber = TestSubscriber.create();

        stateManager.observeStateChanges().subscribe(subscriber);

        commandBus.mapCommandHandler(IncrementCount.class, new RxMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Observable<Collection<CountIncremented>> handle(CountState state, IncrementCount command) {
                return Observable.just((Collection<CountIncremented>)
                        Collections.singleton(new CountIncremented(command.getCount())));
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
    }

    @Test
    public void executeHandler_should_support_non_rx_multi_handler() throws Exception {
        mapCountIncrementedEventHandler();

        TestSubscriber<StateChanged<CountState>> subscriber = TestSubscriber.create();

        stateManager.observeStateChanges().subscribe(subscriber);

        commandBus.mapCommandHandler(IncrementCount.class, new SyncMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Collection<CountIncremented> handle(CountState state, IncrementCount command) {
                return Collections.singleton(new CountIncremented(command.getCount()));
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
    }

    @Test
    public void executeHandler_should_log_rx_multi_handler_exception() throws Exception {
        mapCountIncrementedEventHandler();

        final AtomicReference<Throwable> calledError = new AtomicReference<>();

        loggers.addLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
                calledError.set(error);
            }
        });

        final RuntimeException exception = new RuntimeException("Example error");

        commandBus.mapCommandHandler(IncrementCount.class, new RxMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Observable<Collection<CountIncremented>> handle(CountState state, IncrementCount command) {
                throw exception;
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        assertSame(exception, calledError.get());
    }

    @Test
    public void executeHandler_should_log_rx_multi_handler_error() throws Exception {
        mapCountIncrementedEventHandler();

        final AtomicReference<Throwable> calledError = new AtomicReference<>();

        loggers.addLogger(new DefaultLogger<CountState>() {
            @Override
            public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
                calledError.set(error);
            }
        });

        final RuntimeException exception = new RuntimeException("Example error");

        commandBus.mapCommandHandler(IncrementCount.class, new RxMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
            @Override
            public Observable<Collection<CountIncremented>> handle(CountState state, IncrementCount command) {
                return Observable.error(exception);
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        assertSame(exception, calledError.get());
    }

    @Test
    public void executeHandler_should_not_throw_for_null_result() throws Exception {
        commandBus.mapCommandHandler(IncrementCount.class, new RxCommandHandler<CountState, IncrementCount, Event>() {
            @Override
            public Observable<Event> handle(CountState state, IncrementCount command) {
                return null;
            }
        });

        commandBus.handleCommand(new IncrementCount(1));
    }

    @Test
    public void executeHandler_should_not_throw_for_null_result_from_multi() throws Exception {
        commandBus.mapCommandHandler(IncrementCount.class,
                new RxMultiCommandHandler<CountState, IncrementCount, Event>() {
                    @Override
                    public Observable<Collection<Event>> handle(CountState state, IncrementCount command) {
                        return null;
                    }
                });

        commandBus.handleCommand(new IncrementCount(1));
    }

    @Test
    public void executeHandler_should_add_subscription_single() throws Exception {
        assertFalse(commandBus.subscriptions.hasSubscriptions());

        commandBus.mapCommandHandler(IncrementCount.class,
                new RxCommandHandler<CountState, IncrementCount, CountIncremented>() {
                    @Override
                    public Observable<CountIncremented> handle(CountState state, IncrementCount command) {
                        return Observable.just(new CountIncremented(command.getCount()))
                                // add delay so the subscription remains
                                .delay(1, TimeUnit.MILLISECONDS);
                    }
                });

        eventBus.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return previousState;
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        assertTrue(commandBus.subscriptions.hasSubscriptions());
    }

    @Test
    public void executeHandler_should_add_subscription_multi() throws Exception {
        assertFalse(commandBus.subscriptions.hasSubscriptions());

        commandBus.mapCommandHandler(IncrementCount.class,
                new RxMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {
                    @Override
                    public Observable<Collection<CountIncremented>> handle(CountState state, IncrementCount command) {
                        // noinspection unchecked
                        return (Observable) Observable.just(Collections.singleton(new CountIncremented(1)))
                                // add delay so the subscription remains
                                .delay(1, TimeUnit.MILLISECONDS);
                    }
                });

        eventBus.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return previousState;
            }
        });

        commandBus.handleCommand(new IncrementCount(1));

        assertTrue(commandBus.subscriptions.hasSubscriptions());
    }

    //----------------------------------------------------------------------------------------------
    // HELPER METHODS
    //----------------------------------------------------------------------------------------------

    private void mapCountIncrementedEventHandler() {
        eventBus.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return new CountState(previousState.getCount() + event.getCount());
            }
        });
    }

}
