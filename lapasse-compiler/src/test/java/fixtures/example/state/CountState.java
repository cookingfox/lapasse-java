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
    public String toString() {
        return "CountState{" +
                "count=" + count +
                '}';
    }
}
