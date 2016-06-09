package fixtures.example.command;

import com.cookingfox.lapasse.api.command.Command;

/**
 * Created by abeldebeer on 08/06/16.
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
    public String toString() {
        return "IncrementCount{" +
                "count=" + count +
                '}';
    }
}
