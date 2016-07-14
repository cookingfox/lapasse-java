package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.exception.NotSubscribedException;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
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

    private CountState initialState;
    private DefaultStateManager<CountState> stateManager;

    @Before
    public void setUp() throws Exception {
        initialState = new CountState(0);
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
    // TESTS: dispose
    //----------------------------------------------------------------------------------------------

    @Test
    public void dispose_should_unsubscribe_subscribers() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        OnStateChanged<CountState> subscriber = new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                called.set(true);
            }
        };

        stateManager.subscribe(subscriber);

        stateManager.dispose();

        stateManager.handleNewState(new CountState(1), new CountIncremented(1));

        assertFalse(called.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getCurrentState
    //----------------------------------------------------------------------------------------------

    @Test
    public void getCurrentState_should_return_current_state() throws Exception {
        CountState currentState = stateManager.getCurrentState();

        assertEquals(initialState, currentState);

        CountState newState = new CountState(1);

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
        stateManager.handleNewState(null, new CountIncremented(1));
    }

    @Test(expected = NullPointerException.class)
    public void handleNewState_should_throw_if_event_null() throws Exception {
        stateManager.handleNewState(new CountState(1), null);
    }

    @Test
    public void handleNewState_should_not_set_current_state_if_not_changed() throws Exception {
        CountState newState = new CountState(0);

        stateManager.handleNewState(newState, new CountIncremented(0));

        assertNotSame(newState, stateManager.currentState);
    }

    @Test
    public void handleNewState_should_set_current_state_if_changed() throws Exception {
        CountState newState = new CountState(1);

        stateManager.handleNewState(newState, new CountIncremented(1));

        assertSame(newState, stateManager.currentState);
    }

    @Test
    public void handleNewState_should_notify_subscribers() throws Exception {
        final AtomicReference<CountState> subscriberState = new AtomicReference<>();
        final AtomicReference<Event> subscriberEvent = new AtomicReference<>();

        stateManager.subscribe(new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                subscriberState.set(state);
                subscriberEvent.set(event);
            }
        });

        CountState newState = new CountState(1);
        CountIncremented event = new CountIncremented(1);

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
        stateManager.unsubscribe(new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                // ignore
            }
        });
    }

    @Test
    public void unsubscribe_should_remove_subscriber() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        OnStateChanged<CountState> subscriber = new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                called.set(true);
            }
        };

        stateManager.subscribe(subscriber);
        stateManager.unsubscribe(subscriber);
        stateManager.handleNewState(new CountState(1), new CountIncremented(1));

        assertFalse(called.get());
    }

}
