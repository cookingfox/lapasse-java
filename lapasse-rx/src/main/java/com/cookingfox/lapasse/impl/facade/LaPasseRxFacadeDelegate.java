package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.facade.RxFacade;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.api.state.observer.StateChanged;
import rx.Observable;

/**
 * Delegates all {@link RxFacade} operations to a provided instance.
 */
public class LaPasseRxFacadeDelegate<S extends State>
        extends LaPasseFacadeDelegate<S>
        implements RxFacade<S> {

    /**
     * The Rx facade instance to use as delegate.
     */
    protected final RxFacade<S> rxFacade;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public LaPasseRxFacadeDelegate(RxFacade<S> facade) {
        super(facade);

        rxFacade = facade;
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public Observable<StateChanged<S>> observeStateChanges() {
        return rxFacade.observeStateChanges();
    }

}
