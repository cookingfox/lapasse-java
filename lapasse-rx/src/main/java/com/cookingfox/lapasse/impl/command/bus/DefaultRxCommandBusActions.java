package com.cookingfox.lapasse.impl.command.bus;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.handler.CommandHandler;
import com.cookingfox.lapasse.api.command.handler.MultiCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import rx.functions.Action0;
import rx.functions.Action1;

import java.util.Collection;

/**
 * Helper for re-usable Rx action instances, so they don't need to be recreated for every operation.
 */
enum DefaultRxCommandBusActions {

    //----------------------------------------------------------------------------------------------
    // SINGLETON INSTANCE
    //----------------------------------------------------------------------------------------------

    INSTANCE;

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * References the command bus instance.
     */
    protected DefaultRxCommandBus bus;

    /**
     * References the command to be handled.
     */
    protected Command command;

    /**
     * Whether the handler is a {@link MultiCommandHandler}.
     */
    protected boolean isMultiCommandHandler;

    //----------------------------------------------------------------------------------------------
    // STATIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Preferred method for requesting the necessary set of actions for the command and handler.
     *
     * @param bus     The command bus instance.
     * @param command The command to be handled.
     * @param handler The command handler instance.
     * @return Instance of the helper.
     */
    static DefaultRxCommandBusActions of(DefaultRxCommandBus bus, Command command,
                                         CommandHandler handler) {
        INSTANCE.bus = bus;
        INSTANCE.command = command;
        INSTANCE.isMultiCommandHandler = handler instanceof MultiCommandHandler;

        return INSTANCE;
    }

    //----------------------------------------------------------------------------------------------
    // INSTANCE METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @return The action for when the command handler generates an error.
     */
    Action1<Throwable> getOnError() {
        return isMultiCommandHandler ? onErrorMulti : onError;
    }

    /**
     * @return The action for when the command handler returns null (no Rx Observable).
     */
    Action0 getOnNull() {
        return isMultiCommandHandler ? onNullMulti : onNull;
    }

    /**
     * @param <T> Indicates the concrete type of the value that is handled by the action.
     * @return The action for when the command handler is successful.
     */
    <T> Action1<T> getOnSuccess() {
        //noinspection unchecked
        return (Action1<T>) (isMultiCommandHandler ? onSuccessMulti : onSuccess);
    }

    //----------------------------------------------------------------------------------------------
    // FINAL ACTION IMPLEMENTATIONS
    //----------------------------------------------------------------------------------------------

    private final Action1<Throwable> onError = new Action1<Throwable>() {
        @Override
        public void call(Throwable error) {
            bus.handleResult(error, command, null);
        }
    };

    private final Action1<Throwable> onErrorMulti = new Action1<Throwable>() {
        @Override
        public void call(Throwable error) {
            bus.handleMultiResult(error, command, null);
        }
    };

    private final Action0 onNull = new Action0() {
        @Override
        public void call() {
            bus.handleResult(null, command, null);
        }
    };

    private final Action0 onNullMulti = new Action0() {
        @Override
        public void call() {
            bus.handleMultiResult(null, command, null);
        }
    };

    private final Action1<Event> onSuccess = new Action1<Event>() {
        @Override
        public void call(Event event) {
            bus.handleResult(null, command, event);
        }
    };

    private final Action1<Collection<Event>> onSuccessMulti = new Action1<Collection<Event>>() {
        @Override
        public void call(Collection<Event> events) {
            bus.handleMultiResult(null, command, events);
        }
    };

}
