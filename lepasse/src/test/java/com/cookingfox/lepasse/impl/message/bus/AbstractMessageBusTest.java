package com.cookingfox.lepasse.impl.message.bus;

import fixtures.message.FixtureMessage;
import fixtures.message.bus.FixtureMessageBusImpl;
import fixtures.message.handler.FixtureMessageHandler;
import fixtures.message.store.FixtureMessageStore;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link AbstractMessageBus}.
 */
public class AbstractMessageBusTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private FixtureMessageBusImpl messageBus;
    private FixtureMessageStore messageStore;

    @Before
    public void setUp() throws Exception {
        messageStore = new FixtureMessageStore();
        messageBus = new FixtureMessageBusImpl(messageStore);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: handleMessage
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void handleMessage_null_message_should_throw() throws Exception {
        messageBus.handleMessage(null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: mapMessageHandler
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void mapMessageHandler_null_class_should_throw() throws Exception {
        messageBus.mapMessageHandler(null, new FixtureMessageHandler());
    }

    @Test(expected = NullPointerException.class)
    public void mapMessageHandler_null_handler_should_throw() throws Exception {
        messageBus.mapMessageHandler(FixtureMessage.class, null);
    }

}
