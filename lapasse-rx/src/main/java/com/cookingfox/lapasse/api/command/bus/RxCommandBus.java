package com.cookingfox.lapasse.api.command.bus;

import com.cookingfox.lapasse.api.state.State;
import rx.Scheduler;

/**
 * Extended {@link CommandBus} functionality for Rx support.
 *
 * @param <S> The concrete type of the state object.
 */
public interface RxCommandBus<S extends State> extends CommandBus<S> {

    /**
     * Set the scheduler to observe command handler Observables on.
     *
     * @param observeOnScheduler The scheduler to observe on.
     * @see rx.Observable#observeOn(Scheduler)
     */
    void setCommandObserveScheduler(Scheduler observeOnScheduler);

    /**
     * Set the scheduler to subscribe command handler Observables on.
     *
     * @param subscribeOnScheduler The scheduler to subscribe on.
     * @see rx.Observable#subscribeOn(Scheduler)
     */
    void setCommandSubscribeScheduler(Scheduler subscribeOnScheduler);

}
