package testing;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.OnStateUpdated;

/**
 * Contains methods for testing a state manager implementation.
 *
 * @param <S> The concrete type of the state object.
 */
public interface TestableStateManager<S extends State> extends StateManager<S> {

    /**
     * @return Number of added {@link OnStateChanged} listeners.
     */
    int getStateChangedListenersSize();

    /**
     * @return Number of added {@link OnStateUpdated} listeners.
     */
    int getStateUpdatedListenersSize();

    /**
     * Returns whether the provided listener is added.
     *
     * @param listener The listener instance.
     * @return Whether the provided listener is added.
     */
    boolean hasStateChangedListener(OnStateChanged<S> listener);

    /**
     * Returns whether the provided listener is added.
     *
     * @param listener The listener instance.
     * @return Whether the provided listener is added.
     */
    boolean hasStateUpdatedListener(OnStateUpdated<S> listener);

}
