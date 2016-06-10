package com.cookingfox.lapasse.api.command.logging;

/**
 * Interface for elements that can have a command logger added.
 */
public interface CommandLoggerAware {

    /**
     * Add a command logger.
     *
     * @param logger The command logger to add.
     */
    void addCommandLogger(CommandLogger logger);

}
