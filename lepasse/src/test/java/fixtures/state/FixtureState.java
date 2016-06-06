package fixtures.state;

import com.cookingfox.lepasse.api.state.State;

/**
 * Fixture state object.
 */
public final class FixtureState implements State {

    public final int count;

    public FixtureState(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FixtureState that = (FixtureState) o;

        return count == that.count;
    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return "FixtureState{" +
                "count=" + count +
                '}';
    }

}
