package com.cookingfox.lapasse.compiler.exception;

import javax.lang.model.element.TypeElement;

/**
 * Created by abeldebeer on 18/08/16.
 */
public class AnnotationProcessorException extends Exception {
    protected final TypeElement origin;

    public AnnotationProcessorException(TypeElement origin) {
        this.origin = origin;
    }

    public AnnotationProcessorException(String message, TypeElement origin) {
        super(message);
        this.origin = origin;
    }

    public AnnotationProcessorException(String message, Throwable cause, TypeElement origin) {
        super(message, cause);
        this.origin = origin;
    }

    public AnnotationProcessorException(Throwable cause, TypeElement origin) {
        super(cause);
        this.origin = origin;
    }

    public TypeElement getOrigin() {
        return origin;
    }
}
