package fixtures.message.bus;

import com.cookingfox.lepasse.api.message.Message;
import com.cookingfox.lepasse.api.message.store.MessageStore;
import com.cookingfox.lepasse.impl.message.bus.AbstractMessageBus;
import fixtures.message.FixtureMessage;
import fixtures.message.handler.FixtureMessageHandler;

/**
 * Minimal implementation of {@link AbstractMessageBus} using fixtures.
 */
public class FixtureMessageBusImpl extends AbstractMessageBus<FixtureMessage, FixtureMessageHandler> {

    public FixtureMessageBusImpl(MessageStore messageStore) {
        super(messageStore);
    }

    @Override
    protected void executeHandler(FixtureMessage message, FixtureMessageHandler messageHandler) {
        messageHandler.handle(message);
    }

    @Override
    protected boolean shouldHandleMessage(Message message) {
        return message instanceof FixtureMessage;
    }

}
