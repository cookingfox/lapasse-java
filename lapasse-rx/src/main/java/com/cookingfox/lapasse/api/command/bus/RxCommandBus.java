package com.cookingfox.lapasse.api.command.bus;

import com.cookingfox.lapasse.api.state.State;
import rx.Scheduler;

/**
 * Map command handlers and execute them by handling command objects.
 *
 * @param <S> The concrete type of the state object.
 */
public interface RxCommandBus<S extends State> extends CommandBus<S> {

    void setObserveOnScheduler(Scheduler observeOnScheduler);

    void setSubscribeOnScheduler(Scheduler subscribeOnScheduler);

}
