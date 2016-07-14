package com.cookingfox.lapasse.api.logging;

import com.cookingfox.lapasse.api.state.State;

/**
 * Helper interface for combined logger.
 *
 * @param <S> The concrete type of the state object.
 */
public interface CombinedLoggerHelper<S extends State> extends
        CombinedLogger<S>, CombinedLoggerAware<S> {
}
