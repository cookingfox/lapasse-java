package com.cookingfox.lapasse.impl.event.bus;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.exception.EventHandlerReturnedNullException;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.impl.logging.DefaultLogger;
import com.cookingfox.lapasse.impl.logging.LaPasseLoggers;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import fixtures.message.FixtureMessage;
import fixtures.message.store.FixtureMessageStore;
import fixtures.state.manager.FixtureStateManager;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DefaultEventBus}.
 */
public class DefaultEventBusTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private DefaultEventBus<CountState> eventBus;
    private LaPasseLoggers<CountState> loggers;
    private FixtureMessageStore messageStore;
    private FixtureStateManager stateManager;

    @Before
    public void setUp() throws Exception {
        loggers = new LaPasseLoggers<>();
        messageStore = new FixtureMessageStore();
        stateManager = new FixtureStateManager(new CountState(0));
        eventBus = new DefaultEventBus<>(messageStore, loggers, stateManager);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_message_store_null() throws Exception {
        new DefaultEventBus<>(null, loggers, stateManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_loggers_null() throws Exception {
        new DefaultEventBus<>(messageStore, null, stateManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_state_manager_null() throws Exception {
        new DefaultEventBus<>(messageStore, loggers, null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: executeHandler
    //----------------------------------------------------------------------------------------------

    @Test
    public void executeHandler_should_apply_event_to_state() throws Exception {
        eventBus.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return new CountState(event.getCount());
            }
        });

        final int count = 123;

        eventBus.handleEvent(new CountIncremented(count));

        assertEquals(count, stateManager.getCurrentState().getCount());
    }

    @Test
    public void executeHandler_should_log_error_if_handler_returns_null() throws Exception {
        final AtomicReference<Throwable> calledError = new AtomicReference<>();
        final AtomicReference<Event> calledEvent = new AtomicReference<>();
        final AtomicReference<CountState> calledNewState = new AtomicReference<>();

        eventBus.addEventLogger(new DefaultLogger<CountState>() {
            @Override
            public void onEventHandlerError(Throwable error, Event event, CountState newState) {
                calledError.set(error);
                calledEvent.set(event);
                calledNewState.set(newState);
            }
        });

        eventBus.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return null;
            }
        });

        final Event event = new CountIncremented(1);

        eventBus.handleEvent(event);

        // noinspection all
        assertTrue(calledError.get() instanceof EventHandlerReturnedNullException);
        assertSame(event, calledEvent.get());
        assertNull(calledNewState.get());
    }

    @Test
    public void executeHandler_should_log_error_if_handler_throws() throws Exception {
        final AtomicReference<Throwable> calledError = new AtomicReference<>();
        final AtomicReference<Event> calledEvent = new AtomicReference<>();
        final AtomicReference<CountState> calledNewState = new AtomicReference<>();

        final RuntimeException targetException = new RuntimeException("Example error");

        eventBus.addEventLogger(new DefaultLogger<CountState>() {
            @Override
            public void onEventHandlerError(Throwable error, Event event, CountState newState) {
                calledError.set(error);
                calledEvent.set(event);
                calledNewState.set(newState);
            }
        });

        eventBus.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                throw targetException;
            }
        });

        final Event event = new CountIncremented(1);

        eventBus.handleEvent(event);

        // noinspection all
        assertSame(targetException, calledError.get());
        assertSame(event, calledEvent.get());
        assertNull(calledNewState.get());
    }

    @Test
    public void executeHandler_should_call_logger_with_result() throws Exception {
        final AtomicReference<Event> calledEvent = new AtomicReference<>();
        final AtomicReference<CountState> calledNewState = new AtomicReference<>();

        eventBus.addEventLogger(new DefaultLogger<CountState>() {
            @Override
            public void onEventHandlerResult(Event event, CountState newState) {
                calledEvent.set(event);
                calledNewState.set(newState);
            }
        });

        eventBus.mapEventHandler(CountIncremented.class, new EventHandler<CountState, CountIncremented>() {
            @Override
            public CountState handle(CountState previousState, CountIncremented event) {
                return new CountState(previousState.getCount() + event.getCount());
            }
        });

        final CountIncremented event = new CountIncremented(123);

        eventBus.handleEvent(event);

        assertSame(event, calledEvent.get());
        assertEquals(new CountState(event.getCount()), calledNewState.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: shouldHandleMessageType
    //----------------------------------------------------------------------------------------------

    @Test
    public void shouldHandleMessageType_should_return_true_for_event() throws Exception {
        boolean result = eventBus.shouldHandleMessageType(new CountIncremented(1));

        assertTrue(result);
    }

    @Test
    public void shouldHandleMessageType_should_return_false_for_non_event() throws Exception {
        boolean result = eventBus.shouldHandleMessageType(new FixtureMessage());

        assertFalse(result);
    }

}
