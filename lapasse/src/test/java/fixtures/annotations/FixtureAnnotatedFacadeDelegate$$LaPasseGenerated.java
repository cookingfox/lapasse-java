package fixtures.annotations;

import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.internal.HandlerMapper;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;

/**
 * Fixture generated facade delegate with annotations.
 */
public class FixtureAnnotatedFacadeDelegate$$LaPasseGenerated<T extends FixtureAnnotatedFacadeDelegate> implements HandlerMapper {

    final T origin;
    final Facade<CountState> facade;

    final SyncCommandHandler<CountState, IncrementCount, CountIncremented> _1 = new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {
        @Override
        public CountIncremented handle(CountState state, IncrementCount command) {
            return origin.handleIncrementCount(state, command);
        }
    };

    final EventHandler<CountState, CountIncremented> _2 = new EventHandler<CountState, CountIncremented>() {
        @Override
        public CountState handle(CountState state, CountIncremented event) {
            return origin.handleCountIncremented(state, event);
        }
    };

    public FixtureAnnotatedFacadeDelegate$$LaPasseGenerated(T origin, Facade<CountState> facade) {
        this.origin = origin;
        this.facade = facade;
    }

    @Override
    public void mapHandlers() {
        facade.mapCommandHandler(IncrementCount.class, _1);
        facade.mapEventHandler(CountIncremented.class, _2);
    }

}
