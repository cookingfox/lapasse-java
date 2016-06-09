package com.cookingfox.lapasse.compiler.event;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.compiler.base.AbstractHandleAnnotation;

import javax.lang.model.element.Element;

/**
 * Created by abeldebeer on 09/06/16.
 */
public abstract class AbstractHandleEvent extends AbstractHandleAnnotation {

    protected static final String ANNOTATION = HandleEvent.class.getSimpleName();

    public AbstractHandleEvent(Element element) {
        super(element);
    }

}
