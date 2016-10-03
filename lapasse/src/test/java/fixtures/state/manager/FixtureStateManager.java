package fixtures.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.OnStateUpdated;
import fixtures.example.state.CountState;

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
    public void dispose() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public CountState getCurrentState() {
        return currentState;
    }

    @Override
    public void handleNewState(CountState newState, Event event) {
        currentState = newState;
    }

    @Override
    public void addStateChangedListener(OnStateChanged<CountState> listener) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addStateUpdatedListener(OnStateUpdated<CountState> listener) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeStateChangedListener(OnStateChanged<CountState> listener) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeStateUpdatedListener(OnStateUpdated<CountState> listener) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
