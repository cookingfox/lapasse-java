package com.cookingfox.lapasse.compiler.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.compiler.base.AbstractHandleAnnotation;

import javax.lang.model.element.Element;

/**
 * Base class for {@link HandleCommand} annotation processing.
 */
abstract class AbstractHandleCommand extends AbstractHandleAnnotation {

    protected static final String ANNOTATION = HandleCommand.class.getSimpleName();

    public AbstractHandleCommand(Element element) {
        super(element);
    }

}
