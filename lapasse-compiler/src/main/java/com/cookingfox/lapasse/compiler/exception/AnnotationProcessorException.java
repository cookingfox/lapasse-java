package com.cookingfox.lapasse.compiler.exception;

import javax.lang.model.element.Element;

/**
 * Thrown when an annotation processor exception occurs.
 */
public class AnnotationProcessorException extends Exception {

    protected final Element origin;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public AnnotationProcessorException(String message, Element origin) {
        super(message);

        this.origin = origin;
    }

    //----------------------------------------------------------------------------------------------
    // GETTERS
    //----------------------------------------------------------------------------------------------

    public Element getOrigin() {
        return origin;
    }

}
