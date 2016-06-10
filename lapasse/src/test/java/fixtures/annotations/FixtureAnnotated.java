package fixtures.annotations;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.helper.LaPasseHelper;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;

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
        return new CountIncremented(command.getCount());
    }

    @HandleEvent
    public CountState handleCountIncremented(CountState state, CountIncremented event) {
        return new CountState(state.getCount() + event.getCount());
    }

}
