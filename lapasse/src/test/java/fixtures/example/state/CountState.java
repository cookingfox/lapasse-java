package fixtures.example.state;

import com.cookingfox.lapasse.api.state.State;

/**
 * Fixture state: count.
 */
public final class CountState implements State {
    private final int count;

    public CountState(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CountState && ((CountState) o).count == count;
    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return "CountState{" +
                "count=" + count +
                '}';
    }

    public static CountState createInitialState() {
        return new CountState(0);
    }
}
