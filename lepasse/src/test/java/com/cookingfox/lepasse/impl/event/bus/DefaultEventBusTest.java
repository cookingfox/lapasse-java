package com.cookingfox.lepasse.impl.event.bus;

import com.cookingfox.lepasse.api.event.exception.EventHandlerReturnedNullException;
import com.cookingfox.lepasse.api.event.handler.EventHandler;
import com.cookingfox.lepasse.impl.logging.LePasseLoggers;
import fixtures.event.FixtureCountIncremented;
import fixtures.message.FixtureMessage;
import fixtures.message.store.FixtureMessageStore;
import fixtures.state.FixtureState;
import fixtures.state.manager.FixtureStateManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DefaultEventBus}.
 */
public class DefaultEventBusTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private DefaultEventBus<FixtureState> eventBus;
    private FixtureState initialState;
    private LePasseLoggers<FixtureState> loggers;
    private FixtureMessageStore messageStore;
    private FixtureStateManager stateManager;

    @Before
    public void setUp() throws Exception {
        initialState = new FixtureState(0);
        loggers = new LePasseLoggers<>();
        messageStore = new FixtureMessageStore();
        stateManager = new FixtureStateManager(initialState);
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

    @Test(expected = EventHandlerReturnedNullException.class)
    public void executeHandler_should_throw_if_result_null() throws Exception {
        eventBus.mapEventHandler(FixtureCountIncremented.class, new EventHandler<FixtureState, FixtureCountIncremented>() {
            @Override
            public FixtureState handle(FixtureState previousState, FixtureCountIncremented event) {
                return null;
            }
        });

        eventBus.handleEvent(new FixtureCountIncremented(1));
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
