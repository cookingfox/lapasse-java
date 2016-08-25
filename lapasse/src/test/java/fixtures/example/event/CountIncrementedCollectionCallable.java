package fixtures.example.event;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class CountIncrementedCollectionCallable implements Callable<Collection<CountIncremented>> {
    @Override
    public Collection<CountIncremented> call() throws Exception {
        return Collections.singleton(new CountIncremented(1));
    }
}
