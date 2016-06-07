package fixtures.message;

import com.cookingfox.lapasse.api.message.Message;

import java.util.UUID;

/**
 * Fixture message implementation.
 */
public class FixtureMessage implements Message {

    public final UUID id = UUID.randomUUID();

}
