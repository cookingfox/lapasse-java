package fixtures.command;

import com.cookingfox.lapasse.api.command.Command;

/**
 * Fixture command: increment count.
 */
public class IncrementCount implements Command {

    public final int count;

    public IncrementCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IncrementCount that = (IncrementCount) o;

        return count == that.count;
    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return "IncrementCount{" +
                "count=" + count +
                '}';
    }

}
