package com.cookingfox.lepasse.impl.message.bus;

import com.cookingfox.lepasse.api.command.exception.NoRegisteredCommandErrorHandlerException;
import com.cookingfox.lepasse.api.message.exception.NoMessageHandlersException;
import fixtures.event.FixtureCountIncremented;
import fixtures.message.ExtendedFixtureMessage;
import fixtures.message.FixtureMessage;
import fixtures.message.bus.FixtureMessageBus;
import fixtures.message.handler.FixtureMessageHandler;
import fixtures.message.store.FixtureMessageStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link AbstractMessageBus}.
 */
public class AbstractMessageBusTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private FixtureMessageBus messageBus;
    private FixtureMessageStore messageStore;

    @Before
    public void setUp() throws Exception {
        messageStore = new FixtureMessageStore();
        messageBus = new FixtureMessageBus(messageStore);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_should_subscribe_to_store() throws Exception {
        assertEquals(1, messageStore.getSubscribers().size());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: handleMessage
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void handleMessage_null_message_should_throw() throws Exception {
        messageBus.handleMessage(null);
    }

    @Test(expected = NoMessageHandlersException.class)
    public void handleMessage_should_throw_if_no_mapped_handlers() throws Exception {
        messageBus.handleMessage(new FixtureMessage());
    }

    @Test
    public void handleMessage_should_execute_mapped_handler() throws Exception {
        FixtureMessage message = new FixtureMessage();
        FixtureMessageHandler handler = new FixtureMessageHandler();

        messageBus.mapMessageHandler(FixtureMessage.class, handler);
        messageBus.handleMessage(message);

        assertEquals(1, messageBus.executeHandlerCalls);
        assertEquals(1, handler.handledMessages.size());
        assertTrue(handler.handledMessages.contains(message));
    }

    @Test
    public void handleMessage_should_use_add_message_to_store() throws Exception {
        FixtureMessage message = new FixtureMessage();
        FixtureMessageHandler handler = new FixtureMessageHandler();

        messageBus.mapMessageHandler(FixtureMessage.class, handler);
        messageBus.handleMessage(message);

        assertTrue(messageStore.addedMessages.contains(message));
        assertEquals(1, messageStore.addedMessages.size());
    }

    @Test
    public void handleMessage_should_check_added_message_type() throws Exception {
        FixtureMessage message = new FixtureMessage();
        FixtureMessageHandler handler = new FixtureMessageHandler();

        messageBus.mapMessageHandler(FixtureMessage.class, handler);
        messageBus.handleMessage(message);

        assertEquals(1, messageBus.shouldHandleMessageCalls);
    }

    @Test
    public void handleMessage_should_use_handler_for_message_super_type() throws Exception {
        ExtendedFixtureMessage message = new ExtendedFixtureMessage();
        FixtureMessageHandler handler = new FixtureMessageHandler();

        messageBus.mapMessageHandler(FixtureMessage.class, handler);
        messageBus.handleMessage(message);

        assertEquals(1, messageBus.executeHandlerCalls);
        assertTrue(handler.handledMessages.contains(message));
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

    @Test
    public void mapMessageHandler_should_support_multiple_handlers_for_one_message() throws Exception {
        FixtureMessage message = new FixtureMessage();
        FixtureMessageHandler firstHandler = new FixtureMessageHandler();
        FixtureMessageHandler secondHandler = new FixtureMessageHandler();
        FixtureMessageHandler thirdHandler = new FixtureMessageHandler();

        messageBus.mapMessageHandler(FixtureMessage.class, firstHandler);
        messageBus.mapMessageHandler(FixtureMessage.class, secondHandler);
        messageBus.mapMessageHandler(FixtureMessage.class, thirdHandler);
        messageBus.handleMessage(message);

        assertEquals(3, messageBus.executeHandlerCalls);

        assertEquals(1, firstHandler.handledMessages.size());
        assertTrue(firstHandler.handledMessages.contains(message));

        assertEquals(1, secondHandler.handledMessages.size());
        assertTrue(secondHandler.handledMessages.contains(message));

        assertEquals(1, thirdHandler.handledMessages.size());
        assertTrue(thirdHandler.handledMessages.contains(message));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getMessageHandlers
    //----------------------------------------------------------------------------------------------

    @Test(expected = NoMessageHandlersException.class)
    public void getMessageHandlers_should_throw_for_no_matching_super_type_handlers() throws Exception {
        messageBus.mapMessageHandler(FixtureMessage.class, new FixtureMessageHandler());

        messageBus.getMessageHandlers(FixtureCountIncremented.class);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: onMessageAddedToStore
    //----------------------------------------------------------------------------------------------

    @Test
    public void onMessageAddedToStore_should_not_throw_for_unsupported_message_type() throws Exception {
        messageBus.onMessageAddedToStore.onMessageAdded(new FixtureCountIncremented(123));
    }

}
