package com.cookingfox.lepasse.impl.message.store;

import com.cookingfox.lepasse.api.message.Message;
import com.cookingfox.lepasse.api.message.store.OnMessageAdded;
import fixtures.message.FixtureMessage;
import fixtures.message.store.FixtureMessageStore;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

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

}
