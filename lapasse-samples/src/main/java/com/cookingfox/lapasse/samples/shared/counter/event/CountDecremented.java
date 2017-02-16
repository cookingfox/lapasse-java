package com.cookingfox.lapasse.samples.shared.counter.event;

import com.cookingfox.lapasse.api.event.Event;

/**
 * Created by abeldebeer on 16/02/17.
 */
public final class CountDecremented implements Event {

    @Override
    public String toString() {
        return "CountDecremented{}";
    }

}
