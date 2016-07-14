package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.RxStateManager;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Default implementation of {@link StateManager}.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultRxStateManager<S extends State>
        extends DefaultStateManager<S>
        implements RxStateManager<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public DefaultRxStateManager(S initialState) {
        super(initialState);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public Observable<StateChanged<S>> observeStateChanges() {
        return Observable.create(new Observable.OnSubscribe<StateChanged<S>>() {
            @Override
            public void call(final Subscriber<? super StateChanged<S>> subscriber) {
                // create new listener
                final OnStateChanged<S> listener = new OnStateChanged<S>() {
                    @Override
                    public void onStateChanged(final S state, final Event event) {
                        // wrap parameters with VO
                        subscriber.onNext(new StateChanged<S>() {
                            @Override
                            public Event getEvent() {
                                return event;
                            }

                            @Override
                            public S getState() {
                                return state;
                            }

                            @Override
                            public String toString() {
                                return "StateChanged{" +
                                        "state=" + state +
                                        ", event=" + event +
                                        '}';
                            }
                        });
                    }
                };

                // add listener
                addStateChangedListener(listener);

                /**
                 * Unsubscribe listener on {@link Subscription#unsubscribe()}.
                 */
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        // complete subscriber
                        subscriber.onCompleted();

                        // remove listener
                        removeStateChangedListener(listener);
                    }
                }));
            }
        });
    }

}
