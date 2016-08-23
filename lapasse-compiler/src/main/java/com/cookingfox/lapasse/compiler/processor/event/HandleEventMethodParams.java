package com.cookingfox.lapasse.compiler.processor.event;

import com.cookingfox.lapasse.annotation.HandleEvent;

/**
 * Indicates the method parameters for a {@link HandleEvent} annotated handler method.
 */
public enum HandleEventMethodParams {

    /**
     * Annotated method has no parameters.
     */
    METHOD_NO_PARAMS,

    /**
     * Annotated method has one parameter: the event.
     */
    METHOD_ONE_PARAM_EVENT,

    /**
     * Annotated method has one parameter: the state.
     */
    METHOD_ONE_PARAM_STATE,

    /**
     * Annotated method has two parameters: the first is the event and the second is the state.
     */
    METHOD_TWO_PARAMS_EVENT_STATE,

    /**
     * Annotated method has two parameters: the first is the state and the second is the event.
     */
    METHOD_TWO_PARAMS_STATE_EVENT

}
