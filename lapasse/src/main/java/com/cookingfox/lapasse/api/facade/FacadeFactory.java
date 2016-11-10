package com.cookingfox.lapasse.api.facade;

import com.cookingfox.lapasse.api.state.State;

/**
 * Created by abeldebeer on 10/11/16.
 */
public interface FacadeFactory {

    FacadeFactory addBuilderConfig(FacadeBuilderConfig builderConfig);

    <S extends State> FacadeBuilder<S> newBuilder(S initialState);

    <S extends State> Facade<S> newFacade(S initialState);

}
