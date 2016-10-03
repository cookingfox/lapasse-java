package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.command.bus.CommandBus;
import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.OnStateUpdated;
import com.cookingfox.lapasse.impl.command.bus.DefaultCommandBus;
import com.cookingfox.lapasse.impl.event.bus.DefaultEventBus;
import com.cookingfox.lapasse.impl.logging.DefaultLogger;
import com.cookingfox.lapasse.impl.message.store.NoStorageMessageStore;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;
import testing.TestableLoggersHelper;
import testing.TestableStateManager;

import java.util.Objects;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Abstract class for {@link Facade} tests.
 */
public abstract class AbstractFacadeTest {

    //----------------------------------------------------------------------------------------------
    // TESTS SETUP
    //----------------------------------------------------------------------------------------------

    CommandBus<CountState> commandBus;
    EventBus<CountState> eventBus;
    LaPasseFacade<CountState> facade;
    CountState initialState;
    CombinedLogger<CountState> logger = new DefaultLogger<>();
    TestableLoggersHelper<CountState> loggersHelper;
    MessageStore messageStore;
    TestableStateManager<CountState> stateManager;
    Facade<CountState> testSubject;

    @Before
    public void setUp() throws Exception {
        initialState = new CountState(0);
        loggersHelper = new TestableLoggersHelper<>();
        stateManager = new TestableStateManager<>(initialState);
        messageStore = new NoStorageMessageStore();
        eventBus = new DefaultEventBus<>(messageStore, loggersHelper, stateManager);
        commandBus = new DefaultCommandBus<>(messageStore, eventBus, loggersHelper, stateManager);
        facade = new LaPasseFacade<>(commandBus, eventBus, loggersHelper, messageStore, stateManager);

        testSubject = Objects.requireNonNull(createTestFacade());
    }

    //----------------------------------------------------------------------------------------------
    // ABSTRACT METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @return The Facade instance which is the test subject.
     */
    abstract Facade<CountState> createTestFacade();

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

}
