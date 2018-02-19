package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.impl.event.StringEvent;
import com.cookingfox.lapasse.impl.event.UnspecifiedEvent;
import com.cookingfox.lapasse.impl.util.CollectionUtils;

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
     * Collection of listeners of when the state changes.
     */
    protected final Set<OnStateChanged<S>> stateChangedListeners = CollectionUtils.newConcurrentSet();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public DefaultStateManager(S initialState) {
        currentState = Objects.requireNonNull(initialState, "Initial state can not be null");
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addStateChangedListener(OnStateChanged<S> listener) {
        stateChangedListeners.add(Objects.requireNonNull(listener, "Listener can not be null"));
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
    public void handleNewState(S newState) {
        handleNewState(newState, new UnspecifiedEvent());
    }

    @Override
    public void handleNewState(S newState, String event) {
        handleNewState(newState, new StringEvent(event));
    }

    @Override
    public void handleNewState(S newState, Event event) {
        Objects.requireNonNull(newState, "State can not be null");
        Objects.requireNonNull(event, "Event can not be null");

        if (newState.equals(currentState)) {
            // no state changes
            return;
        }

        currentState = newState;

        for (OnStateChanged<S> listener : stateChangedListeners) {
            listener.onStateChanged(currentState, event);
        }
    }

    @Override
    public void removeStateChangedListener(OnStateChanged<S> listener) {
        Objects.requireNonNull(listener, "Listener can not be null");

        stateChangedListeners.remove(listener);
    }

}
