package fixtures.annotations;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.facade.LaPasseFacadeDelegate;
import com.cookingfox.lapasse.impl.helper.LaPasse;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;

/**
 * Fixture facade delegate with annotations.
 */
public class FixtureAnnotatedFacadeDelegate extends LaPasseFacadeDelegate<CountState> {

    public FixtureAnnotatedFacadeDelegate(Facade<CountState> facade) {
        super(facade);
    }

    public void mapHandlers() {
        LaPasse.mapHandlers(this);
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
