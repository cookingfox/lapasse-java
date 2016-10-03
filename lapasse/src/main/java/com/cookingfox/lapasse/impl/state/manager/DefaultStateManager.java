package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.api.state.observer.OnStateUpdated;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Default implementation of {@link StateManager}.
 *
 * @param <S> The concrete type of the state object.
 */
public class DefaultStateManager<S extends State> implements StateManager<S> {

    /**
     * The current state object.
     */
    protected S currentState;

    /**
     * Collection of listeners for when the state changes.
     */
    protected final Set<OnStateChanged<S>> stateChangedListeners = new LinkedHashSet<>();

    /**
     * Collection of listeners for when the state is updated.
     */
    protected final Set<OnStateUpdated<S>> stateUpdatedListeners = new LinkedHashSet<>();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public DefaultStateManager(S initialState) {
        this.currentState = Objects.requireNonNull(initialState, "Initial state can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addStateChangedListener(OnStateChanged<S> listener) {
        stateChangedListeners.add(Objects.requireNonNull(listener, "Listener can not be null"));
    }

    @Override
    public void addStateUpdatedListener(OnStateUpdated<S> listener) {
        stateUpdatedListeners.add(Objects.requireNonNull(listener, "Listener can not be null"));
    }

    @Override
    public void dispose() {
        stateChangedListeners.clear();
    }

    @Override
    public S getCurrentState() {
        return currentState;
    }

    @Override
    public void handleNewState(S newState, Event event) {
        Objects.requireNonNull(newState, "State can not be null");
        Objects.requireNonNull(event, "Event can not be null");

        boolean noStateChange = newState.equals(currentState);

        currentState = newState;

        // notify state updated
        for (OnStateUpdated<S> listener : stateUpdatedListeners) {
            listener.onStateUpdated(currentState, event);
        }

        if (noStateChange) {
            return;
        }

        // notify state changed
        for (OnStateChanged<S> listener : stateChangedListeners) {
            listener.onStateChanged(currentState, event);
        }
    }

    @Override
    public void removeStateChangedListener(OnStateChanged<S> listener) {
        stateChangedListeners.remove(Objects.requireNonNull(listener, "Listener can not be null"));
    }

    @Override
    public void removeStateUpdatedListener(OnStateUpdated<S> listener) {
        stateUpdatedListeners.remove(Objects.requireNonNull(listener, "Listener can not be null"));
    }

}
