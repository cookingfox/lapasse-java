package testing;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.OnStateUpdated;
import com.cookingfox.lapasse.impl.state.manager.DefaultStateManager;

/**
 * Implementation of {@link DefaultStateManager} which contains extra methods for testing.
 *
 * @param <S> The concrete type of the state object.
 */
public class TestableDefaultStateManager<S extends State>
        extends DefaultStateManager<S>
        implements TestableStateManager<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public TestableDefaultStateManager(S initialState) {
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
