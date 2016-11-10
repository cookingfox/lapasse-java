package com.cookingfox.lapasse.api.facade;

import com.cookingfox.lapasse.api.state.State;

/**
 * Created by abeldebeer on 10/11/16.
 */
public interface FacadeBuilderConfig {

    <S extends State> void configure(FacadeBuilder<S> facadeBuilder);

}
