package com.cookingfox.lapasse.impl.state.observer;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.StateUpdated;

/**
 * Value Object implementation of {@link StateUpdated}.
 */
public class StateUpdatedVo<S extends State> implements StateUpdated<S> {

    protected final Event event;
    protected final S state;

    public StateUpdatedVo(Event event, S state) {
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
        return "StateUpdatedVo{" +
                "event=" + event +
                ", state=" + state +
                '}';
    }

}
