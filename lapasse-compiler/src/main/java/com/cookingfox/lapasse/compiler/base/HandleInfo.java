package com.cookingfox.lapasse.compiler.base;

import com.cookingfox.lapasse.api.state.State;
import com.squareup.javapoet.TypeName;

/**
 * Base interface for handle info object.
 */
public interface HandleInfo extends Validator {

    /**
     * @return The type of the concrete {@link State} parameter, as defined by the implementation.
     */
    TypeName getStateName();

}
