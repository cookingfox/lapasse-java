package com.cookingfox.lapasse.compiler.event;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.compiler.base.AbstractHandleAnnotation;

import javax.lang.model.element.Element;

/**
 * Base class for {@link HandleEvent} annotation processing.
 */
abstract class AbstractHandleEvent extends AbstractHandleAnnotation {

    protected static final String ANNOTATION = HandleEvent.class.getSimpleName();

    public AbstractHandleEvent(Element element) {
        super(element);
    }

}
