package fixtures.message.handler;

import com.cookingfox.lapasse.api.message.handler.MessageHandler;
import fixtures.message.FixtureMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Fixture message handler which stores the 'handled' messages, so they can later be inspected.
 */
public class FixtureMessageHandler implements MessageHandler<FixtureMessage> {

    public final List<FixtureMessage> handledMessages = new LinkedList<>();

    public void handle(FixtureMessage message) {
        handledMessages.add(message);
    }

}
