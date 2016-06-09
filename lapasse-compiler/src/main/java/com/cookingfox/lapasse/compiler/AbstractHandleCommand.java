package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.annotation.HandleCommand;

import javax.lang.model.element.Element;
import java.util.Objects;

/**
 * Created by abeldebeer on 09/06/16.
 */
public abstract class AbstractHandleCommand implements Validator {

    protected static final String ANNOTATION = HandleCommand.class.getSimpleName();

    protected final Element element;
    private boolean isProcessed = false;

    public AbstractHandleCommand(Element element) {
        this.element = Objects.requireNonNull(element);
    }

    public void process() {
        if (isProcessed) {
            return;
        }

        doProcess();

        isProcessed = true;
    }

    protected abstract void doProcess();

}
