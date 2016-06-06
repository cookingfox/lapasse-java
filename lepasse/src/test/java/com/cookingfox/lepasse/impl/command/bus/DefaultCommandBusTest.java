package com.cookingfox.lepasse.impl.command.bus;

import com.cookingfox.lepasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lepasse.api.command.handler.SyncMultiCommandHandler;
import fixtures.command.FixtureIncrementCount;
import fixtures.event.FixtureCountIncremented;
import fixtures.event.bus.FixtureEventBus;
import fixtures.message.FixtureMessage;
import fixtures.message.store.FixtureMessageStore;
import fixtures.state.FixtureState;
import fixtures.state.manager.FixtureStateManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link DefaultCommandBus}.
 */
public class DefaultCommandBusTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private DefaultCommandBus<FixtureState> commandBus;
    private FixtureEventBus eventBus;
    private FixtureMessageStore messageStore;
    private FixtureStateManager stateManager;

    @Before
    public void setUp() throws Exception {
        eventBus = new FixtureEventBus();
        messageStore = new FixtureMessageStore();
        stateManager = new FixtureStateManager(new FixtureState(0));
        commandBus = new DefaultCommandBus<>(messageStore, eventBus, stateManager);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_message_store_null() throws Exception {
        new DefaultCommandBus<>(null, eventBus, stateManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_event_bus_null() throws Exception {
        new DefaultCommandBus<>(messageStore, null, stateManager);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_state_manager_null() throws Exception {
        new DefaultCommandBus<>(messageStore, eventBus, null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: executeHandler
    //----------------------------------------------------------------------------------------------

    @Test
    public void executeHandler_should_pass_event_from_sync_handler() throws Exception {
        final int count = 1;
        final FixtureCountIncremented event = new FixtureCountIncremented(count);

        commandBus.mapCommandHandler(FixtureIncrementCount.class,
                new SyncCommandHandler<FixtureState, FixtureIncrementCount, FixtureCountIncremented>() {
                    @Override
                    public FixtureCountIncremented handle(FixtureState state, FixtureIncrementCount command) {
                        return event;
                    }
                });

        commandBus.handleCommand(new FixtureIncrementCount(count));

        assertTrue(eventBus.handleEventCalls.contains(event));
    }

    @Test
    public void executeHandler_should_pass_events_from_sync_multi_handler() throws Exception {
        final int count = 1;
        final FixtureCountIncremented event = new FixtureCountIncremented(count);

        commandBus.mapCommandHandler(FixtureIncrementCount.class,
                new SyncMultiCommandHandler<FixtureState, FixtureIncrementCount, FixtureCountIncremented>() {
                    @Override
                    public Collection<FixtureCountIncremented> handle(FixtureState state, FixtureIncrementCount command) {
                        return Collections.singletonList(new FixtureCountIncremented(command.count));
                    }
                });

        commandBus.handleCommand(new FixtureIncrementCount(count));

        assertTrue(eventBus.handleEventCalls.contains(event));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: shouldHandleMessageType
    //----------------------------------------------------------------------------------------------

    @Test
    public void shouldHandleMessageType_should_return_true_for_command() throws Exception {
        boolean result = commandBus.shouldHandleMessageType(new FixtureIncrementCount(1));

        assertTrue(result);
    }

    @Test
    public void shouldHandleMessageType_should_return_false_for_non_command() throws Exception {
        boolean result = commandBus.shouldHandleMessageType(new FixtureMessage());

        assertFalse(result);
    }

}
