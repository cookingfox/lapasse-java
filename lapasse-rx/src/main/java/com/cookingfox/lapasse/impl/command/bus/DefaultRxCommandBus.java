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
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import java.util.*;

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

    /**
     * Apply schedulers to Observable.
     *
     * @param observable Result of command handler.
     */
    protected void applySchedulersObservable(Observable<?> observable) {
        if (subscribeOnScheduler != null) {
            observable.subscribeOn(subscribeOnScheduler);
        }

        if (observeOnScheduler != null) {
            observable.observeOn(observeOnScheduler);
        }
    }

    /**
     * Apply schedulers to Single.
     *
     * @param single Result of command handler.
     */
    protected void applySchedulersSingle(Single<?> single) {
        if (subscribeOnScheduler != null) {
            single.subscribeOn(subscribeOnScheduler);
        }

        if (observeOnScheduler != null) {
            single.observeOn(observeOnScheduler);
        }
    }

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

    protected void executeRxCommandHandler(S state, Command command,
                                           CommandHandler<S, Command, Event> handler) {
        Actions actions = Actions.of(this, command, handler);
        Action1<Throwable> onError = actions.getOnError();
        Object source = null;

        try {
            if (handler instanceof RxCommandHandler) {
                source = ((RxCommandHandler<S, Command, Event>) handler).handle(state, command);
            } else if (handler instanceof RxMultiCommandHandler) {
                source = ((RxMultiCommandHandler<S, Command, Event>) handler).handle(state, command);
            } else if (handler instanceof RxSingleCommandHandler) {
                source = ((RxSingleCommandHandler<S, Command, Event>) handler).handle(state, command);
            } else if (handler instanceof RxSingleMultiCommandHandler) {
                source = ((RxSingleMultiCommandHandler<S, Command, Event>) handler).handle(state, command);
            }
        } catch (Throwable error) {
            onError.call(error);
            return;
        }

        if (source == null) {
            actions.getOnNull().call();
            return;
        }

        if (source instanceof Single) {
            Single<?> single = (Single<?>) source;

            applySchedulersSingle(single);

            subscriptions.add(single.subscribe(actions.getOnSuccess(), onError));
        } else {
            Observable<?> observable = (Observable<?>) source;

            applySchedulersObservable(observable);

            subscriptions.add(observable.subscribe(actions.getOnSuccess(), onError));
        }
    }

    protected boolean isSupportedRxCommandHandler(CommandHandler<S, Command, Event> handler) {
        for (Class<? extends CommandHandler> commandHandlerClass : RX_SUPPORTED) {
            if (commandHandlerClass.isInstance(handler)) {
                return true;
            }
        }

        return false;
    }

    //----------------------------------------------------------------------------------------------
    // ENUM: ACTIONS
    //----------------------------------------------------------------------------------------------

    enum Actions {

        //------------------------------------------------------------------------------------------
        // SINGLETON INSTANCE
        //------------------------------------------------------------------------------------------

        INSTANCE;

        //------------------------------------------------------------------------------------------
        // PROPERTIES
        //------------------------------------------------------------------------------------------

        DefaultRxCommandBus bus;
        Command command;
        CommandHandler handler;

        //------------------------------------------------------------------------------------------
        // STATIC METHODS
        //------------------------------------------------------------------------------------------

        static Actions of(DefaultRxCommandBus bus, Command command, CommandHandler handler) {
            INSTANCE.bus = bus;
            INSTANCE.command = command;
            INSTANCE.handler = handler;

            return INSTANCE;
        }

        //------------------------------------------------------------------------------------------
        // INSTANCE METHODS
        //------------------------------------------------------------------------------------------

        Action1<Throwable> getOnError() {
            return handler instanceof MultiCommandHandler ? onErrorMulti : onError;
        }

        Action0 getOnNull() {
            return handler instanceof MultiCommandHandler ? onNullMulti : onNull;
        }

        <T> Action1<T> getOnSuccess() {
            //noinspection unchecked
            return (Action1<T>) (handler instanceof MultiCommandHandler ? onSuccessMulti : onSuccess);
        }

        //------------------------------------------------------------------------------------------
        // FINAL ACTION IMPLEMENTATIONS
        //------------------------------------------------------------------------------------------

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

}
