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
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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

    protected Scheduler observeOnScheduler;
    protected Scheduler subscribeOnScheduler;

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
    public void setObserveOnScheduler(Scheduler observeOnScheduler) {
        this.observeOnScheduler = Objects.requireNonNull(observeOnScheduler,
                "Scheduler can not be null");
    }

    @Override
    public void setSubscribeOnScheduler(Scheduler subscribeOnScheduler) {
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
            ((RxCommandHandler<S, Command, Event>) handler).handle(state, command)
                    .compose(this.<Event>applySchedulers())
                    .subscribe(new Action1<Event>() {
                        @Override
                        public void call(Event event) {
                            handleCommandHandlerResult(null, command, event);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable error) {
                            handleCommandHandlerResult(error, command, null);
                        }
                    });
        } catch (Throwable error) {
            handleCommandHandlerResult(error, command, null);
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
            ((RxMultiCommandHandler<S, Command, Event>) handler).handle(state, command)
                    .compose(this.<Collection<Event>>applySchedulers())
                    .subscribe(new Action1<Collection<Event>>() {
                        @Override
                        public void call(Collection<Event> events) {
                            handleMultiCommandHandlerResult(null, command, events);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable error) {
                            handleMultiCommandHandlerResult(error, command, null);
                        }
                    });
        } catch (Throwable error) {
            handleCommandHandlerResult(error, command, null);
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
