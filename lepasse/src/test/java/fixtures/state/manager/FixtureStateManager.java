package fixtures.state.manager;

import com.cookingfox.lepasse.api.event.Event;
import com.cookingfox.lepasse.api.state.manager.StateManager;
import com.cookingfox.lepasse.api.state.observer.OnStateChanged;
import fixtures.state.FixtureState;

/**
 * Fixture {@link StateManager} implementation for testing purposes only.
 */
public class FixtureStateManager implements StateManager<FixtureState> {

    public FixtureState currentState;

    public FixtureStateManager(FixtureState initialState) {
        this.currentState = initialState;
    }

    @Override
    public void handleNewState(FixtureState newState, Event event) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public FixtureState getCurrentState() {
        return currentState;
    }

    @Override
    public void subscribe(OnStateChanged<FixtureState> subscriber) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
