package testing;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.OnStateUpdated;
import com.cookingfox.lapasse.impl.state.manager.DefaultRxStateManager;

/**
 * Implementation of {@link DefaultRxStateManager} which contains extra methods for testing.
 *
 * @param <S> The concrete type of the state object.
 */
public class TestableDefaultRxStateManager<S extends State>
        extends DefaultRxStateManager<S>
        implements TestableStateManager<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public TestableDefaultRxStateManager(S initialState) {
        super(initialState);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public int getStateChangedListenersSize() {
        return stateChangedListeners.size();
    }

    @Override
    public int getStateUpdatedListenersSize() {
        return stateUpdatedListeners.size();
    }

    @Override
    public boolean hasStateChangedListener(OnStateChanged<S> listener) {
        return stateChangedListeners.contains(listener);
    }

    @Override
    public boolean hasStateUpdatedListener(OnStateUpdated<S> listener) {
        return stateUpdatedListeners.contains(listener);
    }

}
