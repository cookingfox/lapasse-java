package fixtures.command;

import com.cookingfox.lepasse.api.command.Command;

/**
 * Fixture command: increment count.
 */
public class FixtureIncrementCount implements Command {

    public final int count;

    public FixtureIncrementCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FixtureIncrementCount that = (FixtureIncrementCount) o;

        return count == that.count;
    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return "FixtureIncrementCount{" +
                "count=" + count +
                '}';
    }

}
