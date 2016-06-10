package fixtures.example.event;

import com.cookingfox.lapasse.api.event.Event;

/**
 * Fixture event: count incremented.
 */
public final class CountIncremented implements Event {
    private final int count;

    public CountIncremented(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CountIncremented && ((CountIncremented) o).count == count;
    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return "CountIncremented{" +
                "count=" + count +
                '}';
    }
}
