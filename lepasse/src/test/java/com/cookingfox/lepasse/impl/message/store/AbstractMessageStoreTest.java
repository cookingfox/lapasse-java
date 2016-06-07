package com.cookingfox.lepasse.impl.message.store;

import com.cookingfox.lepasse.api.exception.NotSubscribedException;
import com.cookingfox.lepasse.api.message.Message;
import com.cookingfox.lepasse.api.message.store.OnMessageAdded;
import fixtures.message.FixtureMessage;
import fixtures.message.store.FixtureMessageStore;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link AbstractMessageStore}.
 */
public class AbstractMessageStoreTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private AbstractMessageStore messageStore;

    @Before
    public void setUp() throws Exception {
        messageStore = new FixtureMessageStore();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: subscribe
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void subscribe_should_throw_if_null_message() throws Exception {
        messageStore.subscribe(null);
    }

    @Test
    public void subscribe_should_add_subscriber() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        messageStore.subscribe(new OnMessageAdded() {
            @Override
            public void onMessageAdded(Message message) {
                called.set(true);
            }
        });

        messageStore.addMessage(new FixtureMessage());

        assertTrue(called.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: unsubscribe
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void unsubscribe_should_throw_if_null_message() throws Exception {
        messageStore.unsubscribe(null);
    }

    @Test(expected = NotSubscribedException.class)
    public void unsubscribe_should_throw_if_not_subscribed() throws Exception {
        messageStore.unsubscribe(new OnMessageAdded() {
            @Override
            public void onMessageAdded(Message message) {
                // ignore
            }
        });
    }

    @Test
    public void unsubscribe_should_remove_subscriber() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        OnMessageAdded subscriber = new OnMessageAdded() {
            @Override
            public void onMessageAdded(Message message) {
                called.set(true);
            }
        };

        messageStore.subscribe(subscriber);
        messageStore.unsubscribe(subscriber);
        messageStore.addMessage(new FixtureMessage());

        assertFalse(called.get());
    }

}
