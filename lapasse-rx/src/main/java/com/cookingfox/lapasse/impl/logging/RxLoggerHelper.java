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
     * @param commandLoggerAware The subject for which to observe command handler errors.
     * @return An observable for command handler errors.
     */
    public static Observable<CommandHandlerError> observeCommandHandlerErrors(
            final CommandLoggerAware commandLoggerAware) {
        return Observable.create(new Observable.OnSubscribe<CommandHandlerError>() {
            @Override
            public void call(final Subscriber<? super CommandHandlerError> subscriber) {
                // create logger
                final CommandLogger logger = new NoopCommandLogger() {
                    @Override
                    public void onCommandHandlerError(final Throwable e, final Command command,
                                                      final Collection<Event> events) {
                        subscriber.onNext(new CommandHandlerError() {
                            @Override
                            public Command getCommand() {
                                return command;
                            }

                            @Override
                            public Throwable getError() {
                                return e;
                            }

                            @Override
                            public Collection<Event> getEvents() {
                                return events;
                            }
                        });
                    }
                };

                // add logger
                commandLoggerAware.addCommandLogger(logger);

                // remove logger on unsubscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        commandLoggerAware.removeCommandLogger(logger);
                    }
                }));
            }
        });
    }

    /**
     * Creates an observable for command handler results.
     *
     * @param commandLoggerAware The subject for which to observe command handler results.
     * @return An observable for command handler results.
     */
    public static Observable<CommandHandlerResult> observeCommandHandlerResults(
            final CommandLoggerAware commandLoggerAware) {
        return Observable.create(new Observable.OnSubscribe<CommandHandlerResult>() {
            @Override
            public void call(final Subscriber<? super CommandHandlerResult> subscriber) {
                // create logger
                final CommandLogger logger = new NoopCommandLogger() {
                    @Override
                    public void onCommandHandlerResult(final Command command,
                                                       final Collection<Event> events) {
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
                };

                // add logger
                commandLoggerAware.addCommandLogger(logger);

                // remove logger on unsubscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        commandLoggerAware.removeCommandLogger(logger);
                    }
                }));
            }
        });
    }

    /**
     * Creates an observable for event handler errors.
     *
     * @param eventLoggerAware The subject for which to observe event handler errors.
     * @param <S>              The concrete type of the state object.
     * @return An observable for event handler errors.
     */
    public static <S extends State> Observable<EventHandlerError> observeEventHandlerErrors(
            final EventLoggerAware<S> eventLoggerAware) {
        return Observable.create(new Observable.OnSubscribe<EventHandlerError>() {
            @Override
            public void call(final Subscriber<? super EventHandlerError> subscriber) {
                // create logger
                final EventLogger<S> logger = new NoopEventLogger<S>() {
                    @Override
                    public void onEventHandlerError(final Throwable e, final Event event) {
                        subscriber.onNext(new EventHandlerError() {
                            @Override
                            public Event getEvent() {
                                return event;
                            }

                            @Override
                            public Throwable getError() {
                                return e;
                            }
                        });
                    }
                };

                // add logger
                eventLoggerAware.addEventLogger(logger);

                // remove logger on unsubscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        eventLoggerAware.removeEventLogger(logger);
                    }
                }));
            }
        });
    }

    /**
     * Creates an observable for event handler results.
     *
     * @param eventLoggerAware The subject for which to observe event handler results.
     * @param <S>              The concrete type of the state object.
     * @return An observable for event handler results.
     */
    public static <S extends State> Observable<EventHandlerResult> observeEventHandlerResults(
            final EventLoggerAware<S> eventLoggerAware) {
        return Observable.create(new Observable.OnSubscribe<EventHandlerResult>() {
            @Override
            public void call(final Subscriber<? super EventHandlerResult> subscriber) {
                // create logger
                final EventLogger<S> logger = new NoopEventLogger<S>() {
                    @Override
                    public void onEventHandlerResult(final Event event, final S newState) {
                        subscriber.onNext(new EventHandlerResult() {
                            @Override
                            public Event getEvent() {
                                return event;
                            }

                            @Override
                            public State getNewState() {
                                return newState;
                            }
                        });
                    }
                };

                // add logger
                eventLoggerAware.addEventLogger(logger);

                // remove logger on unsubscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        eventLoggerAware.removeEventLogger(logger);
                    }
                }));
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // MEMBER CLASSES
    //----------------------------------------------------------------------------------------------

    /**
     * No-operation implementation of {@link CommandLogger}.
     */
    static class NoopCommandLogger implements CommandLogger {

        @Override
        public void onCommandHandlerError(Throwable e, Command command, Collection<Event> events) {
            // override in subclass
        }

        @Override
        public void onCommandHandlerResult(Command command, Collection<Event> events) {
            // override in subclass
        }

    }

    /**
     * No-operation implementation of {@link EventLogger}.
     *
     * @param <S> The concrete type of the state object.
     */
    static class NoopEventLogger<S extends State> implements EventLogger<S> {

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
