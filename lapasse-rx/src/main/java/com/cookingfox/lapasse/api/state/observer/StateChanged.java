package com.cookingfox.lapasse.api.state.observer;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;

/**
 * Created by abeldebeer on 10/06/16.
 */
public interface StateChanged<S extends State> {

    Event getEvent();

    S getState();

}
