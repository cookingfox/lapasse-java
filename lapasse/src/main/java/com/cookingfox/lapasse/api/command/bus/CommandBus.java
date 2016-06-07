package com.cookingfox.lapasse.api.command.bus;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.handler.CommandHandler;
import com.cookingfox.lapasse.api.command.logging.CommandLoggerAware;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

import java.util.concurrent.ExecutorService;

/**
 * Map command handlers and execute them by handling command objects.
 *
 * @param <S> The concrete type of the state object.
 */
public interface CommandBus<S extends State> extends CommandLoggerAware<S> {

    /**
     * Execute the command handler that is mapped to this command type. Throws if no handler is
     * mapped yet.
     *
     * @param command The command to handle.
     * @see #mapCommandHandler(Class, CommandHandler)
     */
    void handleCommand(Command command);

    /**
     * Map a command handler for a concrete command type.
     *
     * @param commandClass   The concrete command type that this handler will handle.
     * @param commandHandler The handler that is executed when {@link #handleCommand(Command)} is
     *                       called.
     * @param <C>            The concrete command type that this handler will handle.
     * @param <E>            The concrete event type that this handler will produce.
     */
    <C extends Command, E extends Event> void mapCommandHandler(Class<C> commandClass, CommandHandler<S, C, E> commandHandler);

    /**
     * Sets the executor service that will run async command handlers.
     *
     * @param executor The executor service to use.
     */
    void setCommandHandlerExecutor(ExecutorService executor);

}
