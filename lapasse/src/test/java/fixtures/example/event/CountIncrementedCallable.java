package fixtures.example.event;

import java.util.concurrent.Callable;

public final class CountIncrementedCallable implements Callable<CountIncremented> {
    @Override
    public CountIncremented call() throws Exception {
        return new CountIncremented(1);
    }
}
