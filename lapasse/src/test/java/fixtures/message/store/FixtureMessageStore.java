package fixtures.message.store;

import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.message.store.OnMessageAdded;
import com.cookingfox.lapasse.impl.message.store.AbstractMessageStore;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Minimal implementation of {@link AbstractMessageStore} that tracks added messages and immediately
 * calls subscribers.
 */
public class FixtureMessageStore extends AbstractMessageStore {

    public final List<Message> addedMessages = new LinkedList<>();

    @Override
    public void addMessage(Message message) {
        addedMessages.add(Objects.requireNonNull(message));

        notifyMessageAdded(message);
    }

    public Set<OnMessageAdded> getSubscribers() {
        return subscribers;
    }

}
