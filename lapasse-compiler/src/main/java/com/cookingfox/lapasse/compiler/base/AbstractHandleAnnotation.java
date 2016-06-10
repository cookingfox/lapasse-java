package com.cookingfox.lapasse.compiler.base;

import javax.lang.model.element.Element;
import java.util.Objects;

/**
 * Abstract class for processing an annotated element.
 */
public abstract class AbstractHandleAnnotation implements Validator {

    protected final Element element;
    private boolean isProcessed = false;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public AbstractHandleAnnotation(Element element) {
        this.element = Objects.requireNonNull(element);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Process the annotated element. Calls {@link #doProcess()} to do the actual processing and
     * makes sure that method is called only once.
     */
    public final void process() {
        // ensure we only run `doProcess()` once
        if (isProcessed) {
            return;
        }

        doProcess();

        isProcessed = true;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Perform the actual processing.
     */
    protected abstract void doProcess();

}
