package com.cookingfox.lapasse.api.command.logging;

import com.cookingfox.lapasse.api.lifecycle.Disposable;

/**
 * Interface for elements that can have a command logger added.
 */
public interface CommandLoggerAware extends Disposable {

    /**
     * Add a command logger.
     *
     * @param logger The command logger to add.
     */
    void addCommandLogger(CommandLogger logger);

}
