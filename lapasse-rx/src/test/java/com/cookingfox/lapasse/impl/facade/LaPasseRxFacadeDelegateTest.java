package com.cookingfox.lapasse.impl.facade;

import fixtures.example.state.CountState;

/**
 * Unit tests for {@link LaPasseRxFacadeDelegate}.
 */
public class LaPasseRxFacadeDelegateTest
        extends AbstractRxFacadeTest<LaPasseRxFacadeDelegate<CountState>> {

    //----------------------------------------------------------------------------------------------
    // ABSTRACT TEST IMPLEMENTATIONS (SEE SUPER)
    //----------------------------------------------------------------------------------------------

    @Override
    LaPasseRxFacadeDelegate<CountState> createTestFacade() {
        return new LaPasseRxFacadeDelegate<>(facade);
    }

    // TODO: add `dispose` tests

}
