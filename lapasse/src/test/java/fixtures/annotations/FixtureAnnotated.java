package fixtures.annotations;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.helper.LaPasseHelper;
import fixtures.command.IncrementCount;
import fixtures.event.CountIncremented;
import fixtures.state.CountState;

/**
 * Fixture class with annotations.
 */
public class FixtureAnnotated {

    final Facade<CountState> facade;

    public FixtureAnnotated(Facade<CountState> facade) {
        this.facade = facade;
    }

    public void mapHandlers() {
        LaPasseHelper.mapHandlers(this, facade);
    }

    @HandleCommand
    public CountIncremented handleIncrementCount(CountState state, IncrementCount command) {
        return new CountIncremented(command.count);
    }

    @HandleEvent
    public CountState handleCountIncremented(CountState state, CountIncremented event) {
        return new CountState(state.count + event.count);
    }

}
