package com.cookingfox.lapasse.impl.state.manager;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.exception.NotSubscribedException;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.manager.StateManager;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;

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
     * List of subscribers of when the state changes.
     */
    protected final Set<OnStateChanged<S>> subscribers = new LinkedHashSet<>();

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
    public void dispose() {
        subscribers.clear();
    }

    @Override
    public S getCurrentState() {
        return currentState;
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

        for (OnStateChanged<S> subscriber : subscribers) {
            subscriber.onStateChanged(currentState, event);
        }
    }

    @Override
    public void subscribe(OnStateChanged<S> subscriber) {
        subscribers.add(Objects.requireNonNull(subscriber, "Subscriber can not be null"));
    }

    @Override
    public void unsubscribe(OnStateChanged<S> subscriber) {
        Objects.requireNonNull(subscriber, "Subscriber can not be null");

        if (!subscribers.contains(subscriber)) {
            throw new NotSubscribedException(subscriber, this);
        }

        subscribers.remove(subscriber);
    }

}
