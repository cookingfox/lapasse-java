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
public class TestableStateManager<S extends State> extends DefaultStateManager<S> {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public TestableStateManager(S initialState) {
        super(initialState);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Returns whether the provided listener is added.
     *
     * @param listener The listener instance.
     * @return Whether the provided listener is added.
     */
    public boolean hasStateChangedListener(OnStateChanged<S> listener) {
        return stateChangedListeners.contains(listener);
    }

    /**
     * Returns whether the provided listener is added.
     *
     * @param listener The listener instance.
     * @return Whether the provided listener is added.
     */
    public boolean hasStateUpdatedListener(OnStateUpdated<S> listener) {
        return stateUpdatedListeners.contains(listener);
    }

}
