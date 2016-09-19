package com.cookingfox.lapasse.impl.message.store;

import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.store.OnMessageAdded;
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
    // TESTS: dispose
    //----------------------------------------------------------------------------------------------

    @Test
    public void dispose_should_remove_listeners() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        messageStore.addMessageAddedListener(new OnMessageAdded() {
            @Override
            public void onMessageAdded(Message message) {
                called.set(true);
            }
        });

        messageStore.dispose();

        messageStore.addMessage(new FixtureMessage());

        assertFalse(called.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addMessageAddedListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addMessageAddedListener_should_throw_if_null_message() throws Exception {
        messageStore.addMessageAddedListener(null);
    }

    @Test
    public void addMessageAddedListener_should_add_listener() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        messageStore.addMessageAddedListener(new OnMessageAdded() {
            @Override
            public void onMessageAdded(Message message) {
                called.set(true);
            }
        });

        messageStore.addMessage(new FixtureMessage());

        assertTrue(called.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: removeMessageAddedListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void removeMessageAddedListener_should_throw_if_null_message() throws Exception {
        messageStore.removeMessageAddedListener(null);
    }

    @Test
    public void removeMessageAddedListener_should_not_throw_if_not_added() throws Exception {
        messageStore.removeMessageAddedListener(new OnMessageAdded() {
            @Override
            public void onMessageAdded(Message message) {
                // ignore
            }
        });
    }

    @Test
    public void removeMessageAddedListener_should_remove_listener() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        OnMessageAdded listener = new OnMessageAdded() {
            @Override
            public void onMessageAdded(Message message) {
                called.set(true);
            }
        };

        messageStore.addMessageAddedListener(listener);
        messageStore.removeMessageAddedListener(listener);
        messageStore.addMessage(new FixtureMessage());

        assertFalse(called.get());
    }

}
