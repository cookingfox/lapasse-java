package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.RxFacade;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.OnStateUpdated;
import com.cookingfox.lapasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lapasse.impl.logging.DefaultLogger;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;
import testing.TestableDefaultRxStateManager;
import testing.TestableLoggersHelper;
import testing.TestableRxCommandBus;

import java.util.Objects;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Abstract class for {@link RxFacade} tests.
 *
 * @param <T> Indicates the concrete facade implementation which this class will test.
 */
public abstract class AbstractRxFacadeTest<T extends RxFacade<CountState>> {

    //----------------------------------------------------------------------------------------------
    // TESTS SETUP
    //----------------------------------------------------------------------------------------------

    TestableRxCommandBus<CountState> commandBus;
    EventBus<CountState> eventBus;
    LaPasseRxFacade<CountState> facade;
    CountState initialState;
    CombinedLogger<CountState> logger = new DefaultLogger<>();
    TestableLoggersHelper<CountState> loggersHelper;
    MessageStore messageStore;
    TestableDefaultRxStateManager<CountState> stateManager;
    T testSubject;

    @Before
    public void setUp() throws Exception {
        initialState = new CountState(0);
        loggersHelper = new TestableLoggersHelper<>();
        stateManager = new TestableDefaultRxStateManager<>(initialState);
        messageStore = new NoStorageMessageStore();
        eventBus = new DefaultEventBus<>(messageStore, loggersHelper, stateManager);
        commandBus = new TestableRxCommandBus<>(messageStore, eventBus, loggersHelper, stateManager);
        facade = new LaPasseRxFacade<>(commandBus, eventBus, loggersHelper, messageStore, stateManager);

        testSubject = Objects.requireNonNull(createTestFacade());
    }

    //----------------------------------------------------------------------------------------------
    // ABSTRACT METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @return The Facade instance which is the test subject.
     */
    abstract T createTestFacade();

    //----------------------------------------------------------------------------------------------
    // TESTS: FACADE ELEMENTS
    //----------------------------------------------------------------------------------------------

    @Test
    public void commandBus_should_be_ok() throws Exception {
        testSubject.addCommandLogger(logger);

        assertTrue(loggersHelper.hasCommandLogger(logger));

        testSubject.setCommandHandlerExecutor(Executors.newSingleThreadExecutor());

        testSubject.mapCommandHandler(IncrementCount.class, new VoidCommandHandler<CountState, IncrementCount>() {
            @Override
            public void handle(CountState state, IncrementCount command) {
                // ignore
            }
        });

        testSubject.handleCommand(new IncrementCount(1));

        testSubject.removeCommandLogger(logger);

        assertFalse(loggersHelper.hasCommandLogger(logger));
    }

    @Test
    public void rxCommandBus_should_be_ok() throws Exception {
        assertNull(commandBus.getRawObserveOnScheduler());
        assertNull(commandBus.getRawSubscribeOnScheduler());

        Scheduler observeScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        Scheduler subscribeScheduler = Schedulers.from(Executors.newSingleThreadExecutor());

        assertNotSame(observeScheduler, subscribeScheduler);

        testSubject.setCommandObserveScheduler(observeScheduler);
        testSubject.setCommandSubscribeScheduler(subscribeScheduler);

        assertSame(observeScheduler, commandBus.getRawObserveOnScheduler());
        assertSame(subscribeScheduler, commandBus.getRawSubscribeOnScheduler());
    }

    @Test
    public void eventBus_should_be_ok() throws Exception {
        testSubject.addEventLogger(logger);

        assertTrue(loggersHelper.hasEventLogger(logger));

        testSubject.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return new CountState(previousState.getCount() + event.getCount());
            }
        });

        testSubject.handleEvent(new CountIncremented(1));

        testSubject.removeEventLogger(logger);

        assertFalse(loggersHelper.hasEventLogger(logger));
    }

    @Test
    public void loggersHelper_should_be_ok() throws Exception {
        testSubject.addLogger(logger);

        assertTrue(loggersHelper.hasCommandLogger(logger));
        assertTrue(loggersHelper.hasEventLogger(logger));

        testSubject.removeLogger(logger);

        assertFalse(loggersHelper.hasCommandLogger(logger));
        assertFalse(loggersHelper.hasEventLogger(logger));
    }

    @Test
    public void stateManager_should_be_ok() throws Exception {
        OnStateChanged<CountState> onStateChanged = new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                // ignore
            }
        };

        OnStateUpdated<CountState> onStateUpdated = new OnStateUpdated<CountState>() {
            @Override
            public void onStateUpdated(CountState state, Event event) {
                // ignore
            }
        };

        assertSame(initialState, testSubject.getCurrentState());

        testSubject.addStateChangedListener(onStateChanged);
        testSubject.addStateUpdatedListener(onStateUpdated);

        assertTrue(stateManager.hasStateChangedListener(onStateChanged));
        assertTrue(stateManager.hasStateUpdatedListener(onStateUpdated));

        testSubject.removeStateChangedListener(onStateChanged);
        testSubject.removeStateUpdatedListener(onStateUpdated);

        assertFalse(stateManager.hasStateChangedListener(onStateChanged));
        assertFalse(stateManager.hasStateUpdatedListener(onStateUpdated));
    }

    @Test
    public void rxStateManager_should_be_ok() throws Exception {
        Subscription changesSubscription = testSubject.observeStateChanges().subscribe();
        Subscription updatesSubscription = testSubject.observeStateUpdates().subscribe();

        assertEquals(1, stateManager.getStateChangedListenersSize());
        assertEquals(1, stateManager.getStateUpdatedListenersSize());

        changesSubscription.unsubscribe();
        updatesSubscription.unsubscribe();

        assertEquals(0, stateManager.getStateChangedListenersSize());
        assertEquals(0, stateManager.getStateUpdatedListenersSize());
    }

}
