package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.impl.event.StringEvent;
import com.cookingfox.lapasse.impl.event.UnspecifiedEvent;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;
import testing.TestingUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
    public void dispose_should_remove_listeners() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        OnStateChanged<CountState> listener = new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                called.set(true);
            }
        };

        stateManager.addStateChangedListener(listener);

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
    // TESTS: handleNewState(State)
    //----------------------------------------------------------------------------------------------

    @Test
    public void handleNewState_should_notify_listeners_when_unspecified_event() throws Exception {
        final AtomicReference<CountState> listenerState = new AtomicReference<>();
        final AtomicReference<Event> listenerEvent = new AtomicReference<>();

        stateManager.addStateChangedListener(new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                listenerState.set(state);
                listenerEvent.set(event);
            }
        });

        CountState newState = new CountState(1);

        stateManager.handleNewState(newState);

        assertSame(newState, listenerState.get());
        assertTrue(listenerEvent.get() instanceof UnspecifiedEvent);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: handleNewState(State, String)
    //----------------------------------------------------------------------------------------------

    @Test
    public void handleNewState_should_notify_listeners_when_string_event() throws Exception {
        final AtomicReference<CountState> listenerState = new AtomicReference<>();
        final AtomicReference<Event> listenerEvent = new AtomicReference<>();

        stateManager.addStateChangedListener(new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                listenerState.set(state);
                listenerEvent.set(event);
            }
        });

        CountState newState = new CountState(1);
        String event = "example";

        stateManager.handleNewState(newState, event);

        Event eventFromListener = listenerEvent.get();

        assertSame(newState, listenerState.get());
        assertTrue(eventFromListener instanceof StringEvent);
        assertEquals(event, ((StringEvent) eventFromListener).getEvent());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: handleNewState(State, Event)
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void handleNewState_should_throw_if_state_null() throws Exception {
        stateManager.handleNewState(null, new CountIncremented(1));
    }

    @Test(expected = NullPointerException.class)
    public void handleNewState_should_throw_if_event_null() throws Exception {
        stateManager.handleNewState(new CountState(1), (Event) null);
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
    public void handleNewState_should_notify_listeners() throws Exception {
        final AtomicReference<CountState> listenerState = new AtomicReference<>();
        final AtomicReference<Event> listenerEvent = new AtomicReference<>();

        stateManager.addStateChangedListener(new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                listenerState.set(state);
                listenerEvent.set(event);
            }
        });

        CountState newState = new CountState(1);
        CountIncremented event = new CountIncremented(1);

        stateManager.handleNewState(newState, event);

        assertSame(newState, listenerState.get());
        assertSame(event, listenerEvent.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addStateChangedListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addStateChangedListener_should_throw_if_listener_null() throws Exception {
        stateManager.addStateChangedListener(null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: removeStateChangedListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void removeStateChangedListener_should_throw_if_null_message() throws Exception {
        stateManager.removeStateChangedListener(null);
    }

    @Test
    public void removeStateChangedListener_should_not_throw_if_not_added() throws Exception {
        stateManager.removeStateChangedListener(new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                // ignore
            }
        });
    }

    @Test
    public void removeStateChangedListener_should_remove_listener() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        OnStateChanged<CountState> listener = new OnStateChanged<CountState>() {
            @Override
            public void onStateChanged(CountState state, Event event) {
                called.set(true);
            }
        };

        stateManager.addStateChangedListener(listener);
        stateManager.removeStateChangedListener(listener);
        stateManager.handleNewState(new CountState(1), new CountIncremented(1));

        assertFalse(called.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: CONCURRENCY (stateChangedListeners)
    //----------------------------------------------------------------------------------------------

    @Test
    public void stateChangedListeners_should_pass_concurrency_tests() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        final DefaultStateManager<CountState> stateManager = new DefaultStateManager<>(new CountState(counter.get()));

        TestingUtils.runConcurrencyTest(new Runnable() {
            @Override
            public void run() {
                final OnStateChanged<CountState> listener = new OnStateChanged<CountState>() {
                    @Override
                    public void onStateChanged(CountState state, Event event) {
                        // no-op
                    }
                };

                stateManager.addStateChangedListener(listener);
                stateManager.handleNewState(new CountState(counter.incrementAndGet()), new CountIncremented(1));
                stateManager.removeStateChangedListener(listener);
            }
        });
    }

}
