package com.cookingfox.lapasse.impl.event;

import com.cookingfox.lapasse.api.event.Event;

/**
 * Wrapper for a string event.
 */
public class StringEvent implements Event {

    private final String event;

    public StringEvent(final String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringEvent that = (StringEvent) o;

        return event.equals(that.event);
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    @Override
    public String toString() {
        return "Event{'" + event + "'}";
    }
}
