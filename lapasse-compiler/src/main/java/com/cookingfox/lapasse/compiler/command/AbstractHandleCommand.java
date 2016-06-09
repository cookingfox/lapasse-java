package com.cookingfox.lapasse.compiler.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.compiler.base.AbstractHandleAnnotation;

import javax.lang.model.element.Element;

/**
 * Created by abeldebeer on 09/06/16.
 */
public abstract class AbstractHandleCommand extends AbstractHandleAnnotation {

    protected static final String ANNOTATION = HandleCommand.class.getSimpleName();

    public AbstractHandleCommand(Element element) {
        super(element);
    }

}
