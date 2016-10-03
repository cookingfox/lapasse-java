package testing;

import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.impl.logging.DefaultLoggersHelper;

/**
 * Implementation of {@link DefaultLoggersHelper} which contains extra methods for testing.
 *
 * @param <S> The concrete type of the state object.
 */
public class TestableLoggersHelper<S extends State> extends DefaultLoggersHelper<S> {

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Returns whether the provided logger is added.
     *
     * @param logger The logger instance.
     * @return Whether the provided logger is added.
     */
    public boolean hasCommandLogger(CommandLogger logger) {
        return commandLoggers.contains(logger);
    }

    /**
     * Returns whether the provided logger is added.
     *
     * @param logger The logger instance.
     * @return Whether the provided logger is added.
     */
    public boolean hasEventLogger(EventLogger logger) {
        return eventLoggers.contains(logger);
    }

}
