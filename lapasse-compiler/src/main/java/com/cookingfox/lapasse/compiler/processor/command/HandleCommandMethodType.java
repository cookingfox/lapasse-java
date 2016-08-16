package com.cookingfox.lapasse.compiler.processor.command;

/**
 * Created by abeldebeer on 15/08/16.
 */
public enum HandleCommandMethodType {

    /**
     * Annotated method has no parameters.
     */
    METHOD_NO_PARAMS,

    /**
     * Annotated method has one parameter: the command.
     */
    METHOD_ONE_PARAM_COMMAND,

    /**
     * Annotated method has two parameters: the first is the command and the second is the state.
     */
    METHOD_TWO_PARAMS_COMMAND_STATE,

    /**
     * Annotated method has two parameters: the first is the state and the second is the command.
     */
    METHOD_TWO_PARAMS_STATE_COMMAND

}
