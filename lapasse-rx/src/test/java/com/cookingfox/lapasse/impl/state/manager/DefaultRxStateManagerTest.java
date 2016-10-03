package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.api.state.observer.StateUpdated;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;
import rx.Subscription;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link DefaultRxStateManager}.
 */
public class DefaultRxStateManagerTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private final CountState initialState = new CountState(0);
    private DefaultRxStateManager<CountState> stateManager;

    @Before
    public void setUp() throws Exception {
        stateManager = new DefaultRxStateManager<>(initialState);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: observeStateChanges
    //----------------------------------------------------------------------------------------------

    @Test
    public void observeStateChanges_should_observe_state_changes() throws Exception {
        TestSubscriber<StateChanged<CountState>> subscriber = TestSubscriber.create();

        stateManager.observeStateChanges().subscribe(subscriber);

        CountIncremented event = new CountIncremented(1);
        CountState newState = new CountState(event.getCount());

        stateManager.handleNewState(newState, event);

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        StateChanged<CountState> stateChanged = subscriber.getOnNextEvents().get(0);

        assertEquals(event, stateChanged.getEvent());
        assertEquals(newState, stateChanged.getState());

        // test `toString`
        String toString = stateChanged.toString();
        assertTrue(toString.contains("event="));
        assertTrue(toString.contains("state="));
    }

    @Test
    public void observeStateChanges_should_not_emit_if_no_change() throws Exception {
        TestSubscriber<StateChanged<CountState>> subscriber = TestSubscriber.create();

        stateManager.observeStateChanges().subscribe(subscriber);

        // new state equal to initial state
        CountState newState = new CountState(initialState.getCount());
        CountIncremented event = new CountIncremented(newState.getCount());

        stateManager.handleNewState(newState, event);

        subscriber.assertNoErrors();
        subscriber.assertValueCount(0);
    }

    @Test
    public void observeStateChanges_should_add_and_remove_listener() throws Exception {
        TestSubscriber<StateChanged<CountState>> subscriber = TestSubscriber.create();

        Subscription subscription = stateManager.observeStateChanges().subscribe(subscriber);

        assertTrue(stateManager.stateChangedListeners.size() == 1);

        subscription.unsubscribe();

        assertTrue(stateManager.stateChangedListeners.size() == 0);

        stateManager.handleNewState(new CountState(1), new CountIncremented(1));

        subscriber.assertNoErrors();
        subscriber.assertValueCount(0);
        subscriber.assertCompleted();
        subscriber.assertUnsubscribed();
    }

    @Test
    public void observeStateChanges_should_not_throw_on_unsubscribe_already_removed_listener() throws Exception {
        Subscription subscription = stateManager.observeStateChanges().subscribe();

        stateManager.stateChangedListeners.clear();

        subscription.unsubscribe();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: observeStateUpdates
    //----------------------------------------------------------------------------------------------

    @Test
    public void observeStateUpdates_should_observe_state_updates() throws Exception {
        TestSubscriber<StateUpdated<CountState>> subscriber = TestSubscriber.create();

        stateManager.observeStateUpdates().subscribe(subscriber);

        CountIncremented event = new CountIncremented(1);
        CountState newState = new CountState(event.getCount());

        stateManager.handleNewState(newState, event);

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        StateUpdated<CountState> stateUpdated = subscriber.getOnNextEvents().get(0);

        assertEquals(event, stateUpdated.getEvent());
        assertEquals(newState, stateUpdated.getState());

        // test `toString`
        String toString = stateUpdated.toString();
        assertTrue(toString.contains("event="));
        assertTrue(toString.contains("state="));
    }

    @Test
    public void observeStateUpdates_should_emit_if_no_change() throws Exception {
        TestSubscriber<StateUpdated<CountState>> subscriber = TestSubscriber.create();

        stateManager.observeStateUpdates().subscribe(subscriber);

        // new state equal to initial state
        CountState newState = new CountState(initialState.getCount());
        CountIncremented event = new CountIncremented(newState.getCount());

        stateManager.handleNewState(newState, event);

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
    }

    @Test
    public void observeStateUpdates_should_add_and_remove_listener() throws Exception {
        TestSubscriber<StateUpdated<CountState>> subscriber = TestSubscriber.create();

        Subscription subscription = stateManager.observeStateUpdates().subscribe(subscriber);

        assertTrue(stateManager.stateUpdatedListeners.size() == 1);

        subscription.unsubscribe();

        assertTrue(stateManager.stateUpdatedListeners.size() == 0);

        stateManager.handleNewState(new CountState(1), new CountIncremented(1));

        subscriber.assertNoErrors();
        subscriber.assertValueCount(0);
        subscriber.assertCompleted();
        subscriber.assertUnsubscribed();
    }

    @Test
    public void observeStateUpdates_should_not_throw_on_unsubscribe_already_removed_listener() throws Exception {
        Subscription subscription = stateManager.observeStateUpdates().subscribe();

        stateManager.stateUpdatedListeners.clear();

        subscription.unsubscribe();
    }

}
