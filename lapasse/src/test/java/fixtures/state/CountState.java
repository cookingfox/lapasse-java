package fixtures.state;

import com.cookingfox.lapasse.api.state.State;

/**
 * Fixture state: count value.
 */
public final class CountState implements State {

    public final int count;

    public CountState(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountState that = (CountState) o;

        return count == that.count;
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

}
