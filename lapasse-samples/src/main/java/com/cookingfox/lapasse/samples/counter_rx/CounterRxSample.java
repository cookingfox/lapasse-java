package com.cookingfox.lapasse.samples.counter_rx;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import com.cookingfox.lapasse.impl.facade.LaPasseRxFacade;
import com.cookingfox.lapasse.samples.counter_rx.facade.CounterRxFacade;
import com.cookingfox.lapasse.samples.shared.counter.command.DecrementCount;
import com.cookingfox.lapasse.samples.shared.counter.command.IncrementCount;
import com.cookingfox.lapasse.samples.shared.counter.state.CounterState;
import rx.functions.Action1;

import java.util.Collection;

/**
 * Sample application using LaPasse Rx extension, without annotations.
 */
public class CounterRxSample implements CombinedLogger<CounterState> {

    public static void main(String[] args) {
        new CounterRxSample().init();
    }

    private void init() {
        System.out.println("SAMPLE: " + getClass().getSimpleName());

        // create initial state
        CounterState initialState = new CounterState(0);

        // create facade
        CounterRxFacade facade = new CounterRxFacade(new LaPasseRxFacade.Builder<>(initialState).build());

        // observe state changes
        facade.observeStateChanges().subscribe(onStateChanged);

        // log operations
        facade.addLogger(this);

        System.out.println("\nINITIAL STATE: " + initialState);

        System.out.println("\n>>> INCREMENT COUNT");
        facade.handleCommand(new IncrementCount());

        System.out.println("\n>>> DECREMENT COUNT");
        facade.handleCommand(new DecrementCount());
    }

    final Action1<StateChanged<CounterState>> onStateChanged = new Action1<StateChanged<CounterState>>() {
        @Override
        public void call(StateChanged<CounterState> stateChanged) {
            System.out.println("STATE CHANGED: " + stateChanged.getState());
        }
    };

    @Override
    public void onCommandHandlerError(Throwable error, Command command) {
        error.printStackTrace();
    }

    @Override
    public void onCommandHandlerResult(Command command, Collection<Event> events) {
        System.out.println("COMMAND HANDLER RESULT: " + command);
    }

    @Override
    public void onEventHandlerError(Throwable error, Event event) {
        error.printStackTrace();
    }

    @Override
    public void onEventHandlerResult(Event event, CounterState newState) {
        System.out.println("EVENT HANDLER RESULT: " + event);
    }

}
