package com.cookingfox.lapasse.samples.shared.counter.state;

import com.cookingfox.lapasse.api.state.State;

/**
 * Created by abeldebeer on 16/02/17.
 */
public final class CounterState implements State {

    private final int count;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public CounterState(int count) {
        this.count = count;
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    public int getCount() {
        return count;
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CounterState)) return false;

        CounterState that = (CounterState) o;

        return count == that.count;
    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return "CounterState{" +
                "count=" + count +
                '}';
    }

}
