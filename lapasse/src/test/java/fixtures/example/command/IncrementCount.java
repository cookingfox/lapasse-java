package fixtures.example.command;

import com.cookingfox.lapasse.api.command.Command;

/**
 * Fixture command: increment count.
 */
public final class IncrementCount implements Command {
    private final int count;

    public IncrementCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IncrementCount && ((IncrementCount) o).count == count;
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
