package com.cookingfox.lapasse.impl.message.store;

import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.store.OnMessageAdded;
import fixtures.message.FixtureMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertSame;

/**
 * Unit tests for {@link NoStorageMessageStore}.
 */
public class NoStorageMessageStoreTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private NoStorageMessageStore messageStore;

    @Before
    public void setUp() throws Exception {
        messageStore = new NoStorageMessageStore();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addMessage
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addMessage_should_throw_if_message_null() throws Exception {
        messageStore.addMessage(null);
    }

    @Test
    public void addMessage_should_immediately_notify_subscribers() throws Exception {
        final AtomicReference<Message> notifiedMessage = new AtomicReference<>();

        messageStore.subscribe(new OnMessageAdded() {
            @Override
            public void onMessageAdded(Message message) {
                notifiedMessage.set(message);
            }
        });

        Message message = new FixtureMessage();

        messageStore.addMessage(message);

        assertSame(message, notifiedMessage.get());
    }

}
