package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.RxStateManager;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.OnStateUpdated;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.api.state.observer.StateUpdated;
import com.cookingfox.lapasse.impl.state.observer.StateChangedVo;
import com.cookingfox.lapasse.impl.state.observer.StateUpdatedVo;
import rx.Observable;
import rx.Subscriber;
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
                        subscriber.onNext(new StateChangedVo<>(event, state));
                    }
                };

                // add listener
                addStateChangedListener(listener);

                // remove listener on unsubscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        // complete subscriber
                        subscriber.onCompleted();

                        // remove listener, if not yet cleared
                        if (stateChangedListeners.contains(listener)) {
                            removeStateChangedListener(listener);
                        }
                    }
                }));
            }
        });
    }

    @Override
    public Observable<StateUpdated<S>> observeStateUpdates() {
        return Observable.create(new Observable.OnSubscribe<StateUpdated<S>>() {
            @Override
            public void call(final Subscriber<? super StateUpdated<S>> subscriber) {
                // create new listener
                final OnStateUpdated<S> listener = new OnStateUpdated<S>() {
                    @Override
                    public void onStateUpdated(final S state, final Event event) {
                        // wrap parameters with VO
                        subscriber.onNext(new StateUpdatedVo<>(event, state));
                    }
                };

                // add listener
                addStateUpdatedListener(listener);

                // remove listener on unsubscribe
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        // complete subscriber
                        subscriber.onCompleted();

                        // remove listener, if not yet cleared
                        if (stateUpdatedListeners.contains(listener)) {
                            removeStateUpdatedListener(listener);
                        }
                    }
                }));
            }
        });
    }

}
