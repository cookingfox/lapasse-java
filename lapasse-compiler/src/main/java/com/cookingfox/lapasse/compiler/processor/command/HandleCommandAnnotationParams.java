package com.cookingfox.lapasse.compiler.processor.command;

import com.cookingfox.lapasse.annotation.HandleCommand;

/**
 * Indicates the annotation parameters for a {@link HandleCommand} annotated handler method.
 */
public enum HandleCommandAnnotationParams {

    /**
     * Annotation has no parameters.
     */
    ANNOTATION_NO_PARAMS,

    /**
     * Annotation has one parameter: the command.
     */
    ANNOTATION_ONE_PARAM_COMMAND,

    /**
     * Annotation has one parameter: the state.
     */
    ANNOTATION_ONE_PARAM_STATE,

    /**
     * Annotation has two parameters: the command and the state.
     */
    ANNOTATION_TWO_PARAMS_COMMAND_STATE

}
