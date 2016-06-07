package com.cookingfox.lepasse.impl.state.manager;

import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.exception.NotSubscribedException;
import com.cookingfox.lepasse.api.state.observer.OnStateChanged;
import fixtures.event.FixtureCountIncremented;
import fixtures.state.FixtureState;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DefaultStateManager}.
 */
public class DefaultStateManagerTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private FixtureState initialState;
    private DefaultStateManager<FixtureState> stateManager;

    @Before
    public void setUp() throws Exception {
        initialState = new FixtureState(0);
        stateManager = new DefaultStateManager<>(initialState);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_initial_state_null() throws Exception {
        new DefaultStateManager<>(null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getCurrentState
    //----------------------------------------------------------------------------------------------

    @Test
    public void getCurrentState_should_return_current_state() throws Exception {
        FixtureState currentState = stateManager.getCurrentState();

        assertEquals(initialState, currentState);

        FixtureState newState = new FixtureState(1);

        // manually set new current state
        stateManager.currentState = newState;

        currentState = stateManager.getCurrentState();

        assertEquals(newState, currentState);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: handleNewState
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void handleNewState_should_throw_if_state_null() throws Exception {
        stateManager.handleNewState(null, new FixtureCountIncremented(1));
    }

    @Test(expected = NullPointerException.class)
    public void handleNewState_should_throw_if_event_null() throws Exception {
        stateManager.handleNewState(new FixtureState(1), null);
    }

    @Test
    public void handleNewState_should_not_set_current_state_if_not_changed() throws Exception {
        FixtureState newState = new FixtureState(0);

        stateManager.handleNewState(newState, new FixtureCountIncremented(0));

        assertNotSame(newState, stateManager.currentState);
    }

    @Test
    public void handleNewState_should_set_current_state_if_changed() throws Exception {
        FixtureState newState = new FixtureState(1);

        stateManager.handleNewState(newState, new FixtureCountIncremented(1));

        assertSame(newState, stateManager.currentState);
    }

    @Test
    public void handleNewState_should_notify_subscribers() throws Exception {
        final AtomicReference<FixtureState> subscriberState = new AtomicReference<>();
        final AtomicReference<Event> subscriberEvent = new AtomicReference<>();

        stateManager.subscribe(new OnStateChanged<FixtureState>() {
            @Override
            public void onStateChanged(FixtureState state, Event event) {
                subscriberState.set(state);
                subscriberEvent.set(event);
            }
        });

        FixtureState newState = new FixtureState(1);
        FixtureCountIncremented event = new FixtureCountIncremented(1);

        stateManager.handleNewState(newState, event);

        assertSame(newState, subscriberState.get());
        assertSame(event, subscriberEvent.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: subscribe
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void subscribe_should_throw_if_subscriber_null() throws Exception {
        stateManager.subscribe(null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: unsubscribe
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void unsubscribe_should_throw_if_null_message() throws Exception {
        stateManager.unsubscribe(null);
    }

    @Test(expected = NotSubscribedException.class)
    public void unsubscribe_should_throw_if_not_subscribed() throws Exception {
        stateManager.unsubscribe(new OnStateChanged<FixtureState>() {
            @Override
            public void onStateChanged(FixtureState state, Event event) {
                // ignore
            }
        });
    }

    @Test
    public void unsubscribe_should_remove_subscriber() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        OnStateChanged<FixtureState> subscriber = new OnStateChanged<FixtureState>() {
            @Override
            public void onStateChanged(FixtureState state, Event event) {
                called.set(true);
            }
        };

        stateManager.subscribe(subscriber);
        stateManager.unsubscribe(subscriber);
        stateManager.handleNewState(new FixtureState(1), new FixtureCountIncremented(1));

        assertFalse(called.get());
    }

}
