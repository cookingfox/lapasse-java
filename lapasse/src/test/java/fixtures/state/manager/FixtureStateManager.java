package fixtures.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import fixtures.state.CountState;

import java.util.Objects;

/**
 * Fixture {@link StateManager} implementation for testing purposes only.
 */
public class FixtureStateManager implements StateManager<CountState> {

    public CountState currentState;

    public FixtureStateManager(CountState initialState) {
        this.currentState = Objects.requireNonNull(initialState, "Initial state can not be null");
    }

    @Override
    public void handleNewState(CountState newState, Event event) {
        if (!newState.equals(currentState)) {
            currentState = newState;
        }
    }

    @Override
    public CountState getCurrentState() {
        return currentState;
    }

    @Override
    public void subscribe(OnStateChanged<CountState> subscriber) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void unsubscribe(OnStateChanged<CountState> subscriber) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
