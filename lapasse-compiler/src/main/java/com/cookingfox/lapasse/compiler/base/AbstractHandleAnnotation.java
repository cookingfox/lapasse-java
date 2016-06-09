package com.cookingfox.lapasse.compiler.base;

import javax.lang.model.element.Element;
import java.util.Objects;

/**
 * Created by abeldebeer on 09/06/16.
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

    public void process() {
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

    protected abstract void doProcess();

}
