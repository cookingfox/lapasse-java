package com.cookingfox.lapasse.impl.logging;

import com.cookingfox.lapasse.api.state.State;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultLogger}.
 */
public class DefaultLoggerTest {

    @Test
    public void public_methods_should_not_throw_for_null_values() throws Exception {
        DefaultLogger<State> logger = new DefaultLogger<>();
        logger.onCommandHandlerError(null, null, null);
        logger.onCommandHandlerResult(null, null);
        logger.onEventHandlerError(null, null, null);
        logger.onEventHandlerResult(null, null);
    }

}
