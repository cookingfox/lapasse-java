package fixtures.message.store;

import com.cookingfox.lepasse.api.message.Message;
import com.cookingfox.lepasse.api.message.store.MessageStore;
import com.cookingfox.lepasse.api.message.store.OnMessageAdded;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Minimal implementation of {@link MessageStore} that tracks added messages and immediately calls
 * subscribers.
 */
public class FixtureMessageStore implements MessageStore {

    public final List<Message> addedMessages = new LinkedList<>();
    public final List<OnMessageAdded> subscribers = new LinkedList<>();

    @Override
    public void addMessage(Message message) {
        addedMessages.add(Objects.requireNonNull(message));

        for (OnMessageAdded subscriber : subscribers) {
            subscriber.onMessageAdded(message);
        }
    }

    @Override
    public void subscribe(OnMessageAdded subscriber) {
        subscribers.add(Objects.requireNonNull(subscriber));
    }

}
