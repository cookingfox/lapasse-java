package com.cookingfox.lapasse.samples.counter_rx.facade;

import com.cookingfox.lapasse.api.command.handler.RxCommandHandler;
import com.cookingfox.lapasse.api.command.handler.RxSingleCommandHandler;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.facade.RxFacade;
import com.cookingfox.lapasse.impl.facade.LaPasseRxFacadeDelegate;
import com.cookingfox.lapasse.samples.shared.counter.command.DecrementCount;
import com.cookingfox.lapasse.samples.shared.counter.command.IncrementCount;
import com.cookingfox.lapasse.samples.shared.counter.event.CountDecremented;
import com.cookingfox.lapasse.samples.shared.counter.event.CountIncremented;
import com.cookingfox.lapasse.samples.shared.counter.state.CounterState;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * LaPasse {@link Facade} delegate containing the command and event handlers.
 */
public final class CounterRxFacade extends LaPasseRxFacadeDelegate<CounterState> {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public CounterRxFacade(RxFacade<CounterState> facade) {
        super(facade);

        facade.mapCommandHandler(DecrementCount.class, handleDecrementCount);
        facade.mapCommandHandler(IncrementCount.class, handleIncrementCount);

        facade.mapEventHandler(CountDecremented.class, handleCountDecremented);
        facade.mapEventHandler(CountIncremented.class, handleCountIncremented);
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLERS
    //----------------------------------------------------------------------------------------------

    final RxCommandHandler<CounterState, IncrementCount, CountIncremented> handleIncrementCount
            = new RxCommandHandler<CounterState, IncrementCount, CountIncremented>() {
        @Override
        public Observable<CountIncremented> handle(CounterState state, IncrementCount command) {
            return Observable.just(new CountIncremented())
                    // perform on current thread, effectively blocking
                    .delay(500, TimeUnit.MILLISECONDS, Schedulers.immediate());
        }
    };

    final RxSingleCommandHandler<CounterState, DecrementCount, CountDecremented> handleDecrementCount
            = new RxSingleCommandHandler<CounterState, DecrementCount, CountDecremented>() {
        @Override
        public Single<CountDecremented> handle(CounterState state, DecrementCount command) {
            return Single.just(new CountDecremented())
                    // perform on current thread, effectively blocking
                    .delay(500, TimeUnit.MILLISECONDS, Schedulers.immediate());
        }
    };

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLERS
    //----------------------------------------------------------------------------------------------

    final EventHandler<CounterState, CountIncremented> handleCountIncremented
            = new EventHandler<CounterState, CountIncremented>() {
        @Override
        public CounterState handle(CounterState previousState, CountIncremented event) {
            return new CounterState(previousState.getCount() + 1);
        }
    };

    final EventHandler<CounterState, CountDecremented> handleCountDecremented
            = new EventHandler<CounterState, CountDecremented>() {
        @Override
        public CounterState handle(CounterState previousState, CountDecremented event) {
            return new CounterState(previousState.getCount() - 1);
        }
    };

}
