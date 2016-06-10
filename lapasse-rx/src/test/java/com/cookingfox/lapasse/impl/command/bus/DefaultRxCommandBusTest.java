package com.cookingfox.lapasse.impl.command.bus;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.handler.RxCommandHandler;
import com.cookingfox.lapasse.api.command.handler.RxMultiCommandHandler;
import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.command.handler.SyncMultiCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.logging.LoggerCollection;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.manager.RxStateManager;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lapasse.impl.logging.DefaultLogger;
import com.cookingfox.lapasse.impl.logging.LaPasseLoggers;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import com.cookingfox.lapasse.impl.state.manager.DefaultRxStateManager;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertSame;

/**
 * Unit tests for {@link DefaultRxCommandBus}.
 */
public class DefaultRxCommandBusTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private DefaultRxCommandBus<CountState> commandBus;
    private EventBus<CountState> eventBus;
    private LoggerCollection<CountState> loggers;
    private RxStateManager<CountState> stateManager;

    @Before
    public void setUp() throws Exception {
        loggers = new LaPasseLoggers<>();
        MessageStore messageStore = new NoStorageMessageStore();
        stateManager = new DefaultRxStateManager<>(new CountState(0));
        eventBus = new DefaultEventBus<>(messageStore, loggers, stateManager);

        commandBus = new DefaultRxCommandBus<>(messageStore, eventBus, loggers, stateManager);
        commandBus.setObserveOnScheduler(Schedulers.immediate());
        commandBus.setSubscribeOnScheduler(Schedulers.immediate());
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
            public void onCommandHandlerError(Throwable error, Command command, Event... events) {
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
            public void onCommandHandlerError(Throwable error, Command command, Event... events) {
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
            public void onCommandHandlerError(Throwable error, Command command, Event... events) {
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
            public void onCommandHandlerError(Throwable error, Command command, Event... events) {
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
