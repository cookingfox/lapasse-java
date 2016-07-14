package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.state.observer.StateChanged;
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

    private DefaultRxStateManager<CountState> stateManager;

    @Before
    public void setUp() throws Exception {
        stateManager = new DefaultRxStateManager<>(new CountState(0));
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

}
