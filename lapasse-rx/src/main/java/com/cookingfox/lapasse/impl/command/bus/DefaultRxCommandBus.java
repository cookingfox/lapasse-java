package com.cookingfox.lapasse.impl.command.bus;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.bus.RxCommandBus;
import com.cookingfox.lapasse.api.command.handler.CommandHandler;
import com.cookingfox.lapasse.api.command.handler.MultiCommandHandler;
import com.cookingfox.lapasse.api.command.handler.RxCommandHandler;
import com.cookingfox.lapasse.api.command.handler.RxMultiCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.bus.EventBus;
import com.cookingfox.lapasse.api.logging.LoggerCollection;
import com.cookingfox.lapasse.api.message.store.MessageStore;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.RxStateObserver;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.Collection;
import java.util.Objects;

/**
 * Default implementation of {@link RxCommandBus}.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultRxCommandBus<S extends State>
        extends DefaultCommandBus<S>
        implements RxCommandBus<S> {

    //----------------------------------------------------------------------------------------------
    // STATIC INITIALIZER
    //----------------------------------------------------------------------------------------------

    /**
     * @see DefaultCommandBus#SUPPORTED
     */
    static {
        // add supported command handler implementations
        SUPPORTED.add(RxCommandHandler.class);
        SUPPORTED.add(RxMultiCommandHandler.class);
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
                               LoggerCollection<S> loggers,
                               RxStateObserver<S> stateObserver) {
        super(messageStore, eventBus, loggers, stateObserver);
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
     * Apply `observeOn` and `subscribeOn` schedulers to observable.
     *
     * @param <T> The value type that the observable will emit.
     * @return Observable with the schedulers applied.
     */
    protected <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable
                        .subscribeOn(getSubscribeOnScheduler())
                        .observeOn(getObserveOnScheduler());
            }
        };
    }

    @Override
    protected void executeCommandHandler(S state, final Command command,
                                         CommandHandler<S, Command, Event> handler) {
        // not an Rx implementation: use default functionality
        if (!(handler instanceof RxCommandHandler)) {
            super.executeCommandHandler(state, command, handler);
            return;
        }

        try {
            Observable<Event> eventObservable =
                    ((RxCommandHandler<S, Command, Event>) handler).handle(state, command);

            // null result is valid: command handlers are not required to return an event
            if (eventObservable == null) {
                handleResult(null, command, null);
                return;
            }

            // apply schedulers and subscribe to observable
            Subscription subscription = eventObservable
                    .compose(this.<Event>applySchedulers())
                    .subscribe(new Action1<Event>() {
                        @Override
                        public void call(Event event) {
                            handleResult(null, command, event);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable error) {
                            handleResult(error, command, null);
                        }
                    });

            subscriptions.add(subscription);
        } catch (Throwable error) {
            handleResult(error, command, null);
        }
    }

    @Override
    protected void executeMultiCommandHandler(S state, final Command command,
                                              MultiCommandHandler<S, Command, Event> handler) {
        // not an Rx implementation: use default functionality
        if (!(handler instanceof RxMultiCommandHandler)) {
            super.executeMultiCommandHandler(state, command, handler);
            return;
        }

        try {
            Observable<Collection<Event>> multiEventObservable =
                    ((RxMultiCommandHandler<S, Command, Event>) handler).handle(state, command);

            // null result is valid: command handlers are not required to return an event
            if (multiEventObservable == null) {
                handleMultiResult(null, command, null);
                return;
            }

            // apply schedulers and subscribe to observable
            Subscription subscription = multiEventObservable
                    .compose(this.<Collection<Event>>applySchedulers())
                    .subscribe(new Action1<Collection<Event>>() {
                        @Override
                        public void call(Collection<Event> events) {
                            handleMultiResult(null, command, events);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable error) {
                            handleMultiResult(error, command, null);
                        }
                    });

            subscriptions.add(subscription);
        } catch (Throwable error) {
            handleMultiResult(error, command, null);
        }
    }

    /**
     * Returns the `observeOn` scheduler. Sets it to {@link Schedulers#immediate()} if it is null.
     *
     * @return The `observeOn` scheduler.
     */
    protected Scheduler getObserveOnScheduler() {
        if (observeOnScheduler == null) {
            observeOnScheduler = Schedulers.immediate();
        }

        return observeOnScheduler;
    }

    /**
     * Returns the `subscribeOn` scheduler. Sets it to {@link Schedulers#immediate()} if it is null.
     *
     * @return The `subscribeOn` scheduler.
     */
    protected Scheduler getSubscribeOnScheduler() {
        if (subscribeOnScheduler == null) {
            subscribeOnScheduler = Schedulers.immediate();
        }

        return subscribeOnScheduler;
    }

}
