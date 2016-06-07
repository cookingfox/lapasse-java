package com.cookingfox.lepasse.impl.event.bus;

import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.event.exception.EventHandlerReturnedNullException;
import com.cookingfox.lepasse.api.event.handler.EventHandler;
import com.cookingfox.lepasse.impl.logging.DefaultLogger;
import com.cookingfox.lepasse.impl.logging.LePasseLoggers;
import fixtures.event.FixtureCountIncremented;
import fixtures.message.FixtureMessage;
import fixtures.message.store.FixtureMessageStore;
import fixtures.state.FixtureState;
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

    private DefaultEventBus<FixtureState> eventBus;
    private LePasseLoggers<FixtureState> loggers;
    private FixtureMessageStore messageStore;
    private FixtureStateManager stateManager;

    @Before
    public void setUp() throws Exception {
        loggers = new LePasseLoggers<>();
        messageStore = new FixtureMessageStore();
        stateManager = new FixtureStateManager(new FixtureState(0));
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
        eventBus.mapEventHandler(FixtureCountIncremented.class, new EventHandler<FixtureState, FixtureCountIncremented>() {
            @Override
            public FixtureState handle(FixtureState previousState, FixtureCountIncremented event) {
                return new FixtureState(event.count);
            }
        });

        final int count = 123;

        eventBus.handleEvent(new FixtureCountIncremented(count));

        assertEquals(count, stateManager.getCurrentState().count);
    }

    @Test
    public void executeHandler_should_log_error_if_handler_returns_null() throws Exception {
        final AtomicReference<Throwable> calledError = new AtomicReference<>();
        final AtomicReference<Event> calledEvent = new AtomicReference<>();
        final AtomicReference<FixtureState> calledNewState = new AtomicReference<>();

        eventBus.addEventLogger(new DefaultLogger<FixtureState>() {
            @Override
            public void onEventHandlerError(Throwable error, Event event, FixtureState newState) {
                calledError.set(error);
                calledEvent.set(event);
                calledNewState.set(newState);
            }
        });

        eventBus.mapEventHandler(FixtureCountIncremented.class, new EventHandler<FixtureState, FixtureCountIncremented>() {
            @Override
            public FixtureState handle(FixtureState previousState, FixtureCountIncremented event) {
                return null;
            }
        });

        final Event event = new FixtureCountIncremented(1);

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
        final AtomicReference<FixtureState> calledNewState = new AtomicReference<>();

        final RuntimeException targetException = new RuntimeException("Example error");

        eventBus.addEventLogger(new DefaultLogger<FixtureState>() {
            @Override
            public void onEventHandlerError(Throwable error, Event event, FixtureState newState) {
                calledError.set(error);
                calledEvent.set(event);
                calledNewState.set(newState);
            }
        });

        eventBus.mapEventHandler(FixtureCountIncremented.class, new EventHandler<FixtureState, FixtureCountIncremented>() {
            @Override
            public FixtureState handle(FixtureState previousState, FixtureCountIncremented event) {
                throw targetException;
            }
        });

        final Event event = new FixtureCountIncremented(1);

        eventBus.handleEvent(event);

        // noinspection all
        assertSame(targetException, calledError.get());
        assertSame(event, calledEvent.get());
        assertNull(calledNewState.get());
    }

    @Test
    public void executeHandler_should_call_logger_with_result() throws Exception {
        final AtomicReference<Event> calledEvent = new AtomicReference<>();
        final AtomicReference<FixtureState> calledNewState = new AtomicReference<>();

        eventBus.addEventLogger(new DefaultLogger<FixtureState>() {
            @Override
            public void onEventHandlerResult(Event event, FixtureState newState) {
                calledEvent.set(event);
                calledNewState.set(newState);
            }
        });

        eventBus.mapEventHandler(FixtureCountIncremented.class, new EventHandler<FixtureState, FixtureCountIncremented>() {
            @Override
            public FixtureState handle(FixtureState previousState, FixtureCountIncremented event) {
                return new FixtureState(previousState.count + event.count);
            }
        });

        final FixtureCountIncremented event = new FixtureCountIncremented(123);

        eventBus.handleEvent(event);

        assertSame(event, calledEvent.get());
        assertEquals(new FixtureState(event.count), calledNewState.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: shouldHandleMessageType
    //----------------------------------------------------------------------------------------------

    @Test
    public void shouldHandleMessageType_should_return_true_for_event() throws Exception {
        boolean result = eventBus.shouldHandleMessageType(new FixtureCountIncremented(1));

        assertTrue(result);
    }

    @Test
    public void shouldHandleMessageType_should_return_false_for_non_event() throws Exception {
        boolean result = eventBus.shouldHandleMessageType(new FixtureMessage());

        assertFalse(result);
    }

}
