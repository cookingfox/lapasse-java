package fixtures.message.bus;

import com.cookingfox.lepasse.api.message.Message;
import com.cookingfox.lepasse.api.message.store.MessageStore;
import com.cookingfox.lepasse.impl.message.bus.AbstractMessageBus;
import fixtures.message.FixtureMessage;
import fixtures.message.handler.FixtureMessageHandler;

/**
 * Minimal implementation of {@link AbstractMessageBus} using fixtures.
 */
public class FixtureMessageBus extends AbstractMessageBus<FixtureMessage, FixtureMessageHandler> {

    public int executeHandlerCalls = 0;
    public int shouldHandleMessageCalls = 0;

    public FixtureMessageBus(MessageStore messageStore) {
        super(messageStore);
    }

    @Override
    protected void executeHandler(FixtureMessage message, FixtureMessageHandler messageHandler) {
        executeHandlerCalls++;

        messageHandler.handle(message);
    }

    @Override
    protected boolean shouldHandleMessageType(Message message) {
        shouldHandleMessageCalls++;

        return message instanceof FixtureMessage;
    }

}
