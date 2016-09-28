package com.cookingfox.lapasse.impl.logging;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.logging.CommandHandlerError;
import com.cookingfox.lapasse.api.command.logging.CommandHandlerResult;
import com.cookingfox.lapasse.api.command.logging.CommandLogger;
import com.cookingfox.lapasse.api.command.logging.CommandLoggerAware;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.logging.EventHandlerError;
import com.cookingfox.lapasse.api.event.logging.EventHandlerResult;
import com.cookingfox.lapasse.api.event.logging.EventLogger;
import com.cookingfox.lapasse.api.event.logging.EventLoggerAware;
import com.cookingfox.lapasse.api.state.State;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * Rx helper methods for logging features.
 */
public final class RxLoggerHelper {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR (disabled)
    //----------------------------------------------------------------------------------------------

    private RxLoggerHelper() {
        throw new UnsupportedOperationException();
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates an observable for command handler errors.
     *
     * @param loggerAware The subject for which to observe command handler errors.
     * @return An observable for command handler errors.
     */
    public static Observable<CommandHandlerError> observeCommandHandlerErrors(
            CommandLoggerAware loggerAware) {
        return logCommand(requireNonNull(loggerAware), new RxCommandLogger<CommandHandlerError>() {
            @Override
            public void onCommandHandlerError(final Throwable e, final Command command) {
                subscriber.onNext(new CommandHandlerError() {
                    @Override
                    public Command getCommand() {
                        return command;
                    }

                    @Override
                    public Throwable getError() {
                        return e;
                    }
                });
            }
        });
    }

    /**
     * Creates an observable for command handler results.
     *
     * @param loggerAware The subject for which to observe command handler results.
     * @return An observable for command handler results.
     */
    public static Observable<CommandHandlerResult> observeCommandHandlerResults(
            CommandLoggerAware loggerAware) {
        return logCommand(requireNonNull(loggerAware), new RxCommandLogger<CommandHandlerResult>() {
            @Override
            public void onCommandHandlerResult(final Command command, final Collection<Event> events) {
                subscriber.onNext(new CommandHandlerResult() {
                    @Override
                    public Command getCommand() {
                        return command;
                    }

                    @Override
                    public Collection<Event> getEvents() {
                        return events;
                    }
                });
            }
        });
    }

    /**
     * Creates an observable for event handler errors.
     *
     * @param loggerAware The subject for which to observe event handler errors.
     * @param <S>         The concrete type of the state object.
     * @return An observable for event handler errors.
     */
    public static <S extends State> Observable<EventHandlerError> observeEventHandlerErrors(
            EventLoggerAware<S> loggerAware) {
        return logEvent(requireNonNull(loggerAware), new RxEventLogger<S, EventHandlerError>() {
            @Override
            public void onEventHandlerError(final Throwable e, final Event event) {
                subscriber.onNext(new EventHandlerError() {
                    @Override
                    public Throwable getError() {
                        return e;
                    }

                    @Override
                    public Event getEvent() {
                        return event;
                    }
                });
            }
        });
    }

    /**
     * Creates an observable for event handler results.
     *
     * @param loggerAware The subject for which to observe event handler results.
     * @param <S>         The concrete type of the state object.
     * @return An observable for event handler results.
     */
    public static <S extends State> Observable<EventHandlerResult> observeEventHandlerResults(
            EventLoggerAware<S> loggerAware) {
        return logEvent(requireNonNull(loggerAware), new RxEventLogger<S, EventHandlerResult>() {
            @Override
            public void onEventHandlerResult(final Event event, final S newState) {
                subscriber.onNext(new EventHandlerResult<S>() {
                    @Override
                    public Event getEvent() {
                        return event;
                    }

                    @Override
                    public S getNewState() {
                        return newState;
                    }
                });
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // PRIVATE METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates an observable for the provided logger implementation. Adds the logger when subscribed
     * to the observable and removes it when unsubscribed.
     *
     * @param loggerAware The subject for which to observe handler log calls.
     * @param logger      The logger implementation.
     * @param <T>         The concrete log call VO that will be emitted by the observable.
     * @return Observable for the provided log call VO.
     */
    static <T> Observable<T> logCommand(final CommandLoggerAware loggerAware,
                                        final RxCommandLogger<T> logger) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                // set subscriber reference for logger
                logger.subscriber = subscriber;

                // add logger
                loggerAware.addCommandLogger(logger);

                // remove logger on subscriber unsubscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        loggerAware.removeCommandLogger(logger);
                    }
                }));
            }
        });
    }

    /**
     * * Creates an observable for the provided logger implementation. Adds the logger when subscribed
     * to the observable and removes it when unsubscribed.
     *
     * @param loggerAware The subject for which to observe handler log calls.
     * @param logger      The logger implementation.
     * @param <S>         The concrete type of the state object.
     * @param <T>         The concrete log call VO that will be emitted by the observable.
     * @return Observable for the provided log call VO.
     */
    static <S extends State, T> Observable<T> logEvent(final EventLoggerAware<S> loggerAware,
                                                       final RxEventLogger<S, T> logger) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                // set subscriber reference for logger
                logger.subscriber = subscriber;

                // add logger
                loggerAware.addEventLogger(logger);

                // remove logger on subscriber unsubscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        loggerAware.removeEventLogger(logger);
                    }
                }));
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // MEMBER CLASSES
    //----------------------------------------------------------------------------------------------

    /**
     * Helper class for usage with Rx subscriber.
     *
     * @param <T> The concrete type of the value this subscriber will receive.
     */
    static abstract class SubscriberAware<T> {

        /**
         * The subscriber reference.
         */
        Subscriber<? super T> subscriber;

    }

    /**
     * No-operation implementation of {@link CommandLogger} that is {@link SubscriberAware}.
     *
     * @param <T> The concrete type of the value which will be received by the subscriber.
     */
    static class RxCommandLogger<T> extends SubscriberAware<T> implements CommandLogger {

        @Override
        public void onCommandHandlerError(Throwable e, Command command) {
            // override in subclass
        }

        @Override
        public void onCommandHandlerResult(Command command, Collection<Event> events) {
            // override in subclass
        }

    }

    /**
     * No-operation implementation of {@link EventLogger} that is {@link SubscriberAware}.
     *
     * @param <S> The concrete type of the state object.
     * @param <T> The concrete type of the value which will be received by the subscriber.
     */
    static class RxEventLogger<S extends State, T>
            extends SubscriberAware<T> implements EventLogger<S> {

        @Override
        public void onEventHandlerError(Throwable e, Event event) {
            // override in subclass
        }

        @Override
        public void onEventHandlerResult(Event event, S newState) {
            // override in subclass
        }

    }

}
