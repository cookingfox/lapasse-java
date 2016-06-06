package fixtures.event;

import com.cookingfox.lepasse.api.event.Event;

/**
 * Fixture event: count incremented.
 */
public class FixtureCountIncremented implements Event {

    public final int count;

    public FixtureCountIncremented(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FixtureCountIncremented that = (FixtureCountIncremented) o;

        return count == that.count;
    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return "FixtureCountIncremented{" +
                "count=" + count +
                '}';
    }

}
