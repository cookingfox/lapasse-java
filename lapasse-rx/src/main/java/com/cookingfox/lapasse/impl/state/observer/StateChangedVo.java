package com.cookingfox.lapasse.impl.state.observer;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.StateChanged;

/**
 * Value Object implementation of {@link StateChanged}.
 */
public class StateChangedVo<S extends State> implements StateChanged<S> {

    protected final Event event;
    protected final S state;

    public StateChangedVo(Event event, S state) {
        this.event = event;
        this.state = state;
    }

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
        return "StateChangedVo{" +
                "event=" + event +
                ", state=" + state +
                '}';
    }

}
