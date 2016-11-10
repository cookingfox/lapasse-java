package com.cookingfox.lapasse.impl.facade;

import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.facade.FacadeBuilder;
import com.cookingfox.lapasse.api.facade.FacadeBuilderConfig;
import com.cookingfox.lapasse.api.facade.FacadeFactory;
import com.cookingfox.lapasse.api.state.State;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Default implementation of {@link FacadeFactory}.
 */
public class LaPasseFacadeFactory implements FacadeFactory {

    protected final Set<FacadeBuilderConfig> builderConfigs = new LinkedHashSet<>();

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public FacadeFactory addBuilderConfig(FacadeBuilderConfig builderConfig) {
        builderConfigs.add(Objects.requireNonNull(builderConfig, "Builder config can not be null"));

        return this;
    }

    @Override
    public <S extends State> FacadeBuilder<S> newBuilder(S initialState) {
        return applyBuilderConfigs(new LaPasseFacade.Builder<>(initialState));
    }

    @Override
    public <S extends State> Facade<S> newFacade(S initialState) {
        return newBuilder(initialState).build();
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    protected <S extends State> FacadeBuilder<S> applyBuilderConfigs(FacadeBuilder<S> builder) {
        for (FacadeBuilderConfig config : builderConfigs) {
            config.configure(builder);
        }

        return builder;
    }

}
