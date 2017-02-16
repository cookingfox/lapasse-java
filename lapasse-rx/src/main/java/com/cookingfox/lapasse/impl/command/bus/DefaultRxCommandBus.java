package com.cookingfox.lapasse.impl.command.bus;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.bus.RxCommandBus;
import com.cookingfox.lapasse.api.command.handler.*;
import com.cookingfox.lapasse.api.command.logging.CommandLoggerHelper;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Default implementation of {@link RxCommandBus}.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultRxCommandBus<S extends State>
        extends DefaultCommandBus<S>
        implements RxCommandBus<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------

    /**
     * Set of supported command handlers.
     */
    protected static final Set<Class<? extends CommandHandler>> RX_SUPPORTED;

    static {
        // define supported RX command handler implementations
        final Set<Class<? extends CommandHandler>> rxSupported = new LinkedHashSet<>();
        rxSupported.add(RxCommandHandler.class);
        rxSupported.add(RxMultiCommandHandler.class);
        rxSupported.add(RxSingleCommandHandler.class);
        rxSupported.add(RxSingleMultiCommandHandler.class);
        RX_SUPPORTED = Collections.unmodifiableSet(rxSupported);

        // add to all supported command handler implementations
        SUPPORTED.addAll(RX_SUPPORTED);
    }

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * The default Scheduler that will be observed on.
     *
     * @see Observable#observeOn(Scheduler)
     */
    protected Scheduler observeOnScheduler;

    /**
     * The default Scheduler that will be subscribed on.
     *
     * @see Observable#subscribeOn(Scheduler)
     */
    protected Scheduler subscribeOnScheduler;

    /**
     * Managed collection of Rx subscriptions.
     */
    protected final CompositeSubscription subscriptions = new CompositeSubscription();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public DefaultRxCommandBus(MessageStore messageStore,
                               EventBus<S> eventBus,
                               CommandLoggerHelper loggerHelper,
                               RxStateObserver<S> stateObserver) {
        super(messageStore, eventBus, loggerHelper, stateObserver);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void dispose() {
        super.dispose();

        subscriptions.unsubscribe();
    }

    @Override
    public void setCommandObserveScheduler(Scheduler observeOnScheduler) {
        this.observeOnScheduler = Objects.requireNonNull(observeOnScheduler,
                "Scheduler can not be null");
    }

    @Override
    public void setCommandSubscribeScheduler(Scheduler subscribeOnScheduler) {
        this.subscribeOnScheduler = Objects.requireNonNull(subscribeOnScheduler,
                "Scheduler can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void executeCommandHandler(S state, final Command command,
                                         CommandHandler<S, Command, Event> handler) {
        if (isSupportedRxCommandHandler(handler)) {
            executeRxCommandHandler(state, command, handler);
        } else {
            super.executeCommandHandler(state, command, handler);
        }
    }

    @Override
    protected void executeMultiCommandHandler(S state, final Command command,
                                              MultiCommandHandler<S, Command, Event> handler) {
        if (isSupportedRxCommandHandler(handler)) {
            executeRxCommandHandler(state, command, handler);
        } else {
            super.executeMultiCommandHandler(state, command, handler);
        }
    }

    /**
     * Execute the provided Rx command handler.
     *
     * @param state   The current state object.
     * @param command The command object.
     * @param handler The Rx command handler to execute.
     */
    protected void executeRxCommandHandler(S state, Command command,
                                           CommandHandler<S, Command, Event> handler) {
        // get re-usable Rx actions for this operation
        DefaultRxCommandBusActions actions = DefaultRxCommandBusActions.of(this, command, handler);

        // on error action
        Action1<Throwable> onError = actions.getOnError();

        // the Rx observable / single that was returned by the command handler.
        Object rx;

        // execute handler: returns Rx observable / single
        try {
            if (handler instanceof RxCommandHandler) {
                rx = ((RxCommandHandler<S, Command, Event>) handler).handle(state, command);
            } else if (handler instanceof RxMultiCommandHandler) {
                rx = ((RxMultiCommandHandler<S, Command, Event>) handler).handle(state, command);
            } else if (handler instanceof RxSingleCommandHandler) {
                rx = ((RxSingleCommandHandler<S, Command, Event>) handler).handle(state, command);
            } else {
                rx = ((RxSingleMultiCommandHandler<S, Command, Event>) handler).handle(state, command);
            }
        } catch (Throwable error) {
            onError.call(error);
            return;
        }

        // `null` result is valid: command handlers are not required to return an event
        if (rx == null) {
            actions.getOnNull().call();
            return;
        }

        if (rx instanceof Single) {
            Single<?> single = (Single<?>) rx;

            // apply schedulers if set
            if (subscribeOnScheduler != null) {
                single.subscribeOn(subscribeOnScheduler);
            }
            if (observeOnScheduler != null) {
                single.observeOn(observeOnScheduler);
            }

            // perform the operation
            subscriptions.add(single.subscribe(actions.getOnSuccess(), onError));
        } else {
            Observable<?> observable = (Observable<?>) rx;

            // apply schedulers if set
            if (subscribeOnScheduler != null) {
                observable.subscribeOn(subscribeOnScheduler);
            }
            if (observeOnScheduler != null) {
                observable.observeOn(observeOnScheduler);
            }

            // perform the operation
            subscriptions.add(observable.subscribe(actions.getOnSuccess(), onError));
        }
    }

    /**
     * Returns whether the provided command handler is supported by this Rx command bus.
     *
     * @param handler Command handler.
     * @return Whether the provided command handler is supported by this Rx command bus.
     */
    protected boolean isSupportedRxCommandHandler(CommandHandler<S, Command, Event> handler) {
        for (Class<? extends CommandHandler> commandHandlerClass : RX_SUPPORTED) {
            if (commandHandlerClass.isInstance(handler)) {
                return true;
            }
        }

        return false;
    }

}
