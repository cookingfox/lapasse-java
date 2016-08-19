package com.cookingfox.lapasse.compiler.processor.event;

import com.cookingfox.lapasse.annotation.HandleEvent;

/**
 * Indicates the annotation parameters for a {@link HandleEvent} annotated handler method.
 */
public enum HandleEventAnnotationType {

    /**
     * Annotation has no parameters.
     */
    ANNOTATION_NO_PARAMS,

    /**
     * Annotation has one parameter: the event.
     */
    ANNOTATION_ONE_PARAM_EVENT

}
