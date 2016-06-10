package fixtures.event;

import com.cookingfox.lapasse.api.event.Event;

/**
 * Fixture event: count incremented.
 */
public class CountIncremented implements Event {

    public final int count;

    public CountIncremented(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountIncremented that = (CountIncremented) o;

        return count == that.count;
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
