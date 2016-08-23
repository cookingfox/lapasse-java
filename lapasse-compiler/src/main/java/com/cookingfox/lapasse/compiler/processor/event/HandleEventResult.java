package com.cookingfox.lapasse.compiler.processor.event;

import com.cookingfox.lapasse.annotation.HandleEvent;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Result of a processed {@link HandleEvent} annotated handler method.
 */
public class HandleEventResult {

    /**
     * Event type, defined by the annotation.
     */
    protected TypeMirror annotationEventType;

    /**
     * Indicates the annotation's parameters.
     */
    protected HandleEventAnnotationParams annotationParams;

    /**
     * The source element.
     */
    protected final Element element;

    /**
     * Event type, defined by the handler method parameters.
     */
    protected TypeMirror eventType;

    /**
     * Name of the handler method.
     */
    protected Name methodName;

    /**
     * Indicates the handler method's parameters.
     */
    protected HandleEventMethodParams methodParams;

    /**
     * The handler method parameters.
     */
    protected List<? extends VariableElement> parameters;

    /**
     * State type, defined by the handler method return type.
     */
    protected TypeMirror stateType;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleEventResult(Element element) {
        this.element = element;
    }

    //----------------------------------------------------------------------------------------------
    // GETTERS
    //----------------------------------------------------------------------------------------------

    /**
     * @return Event type, defined by the annotation.
     */
    public TypeMirror getAnnotationEventType() {
        return annotationEventType;
    }

    /**
     * @return Indicates the annotation's parameters.
     */
    public HandleEventAnnotationParams getAnnotationParams() {
        return annotationParams;
    }

    /**
     * @return The source element.
     */
    public Element getElement() {
        return element;
    }

    /**
     * @return Event type, defined by the handler method parameters.
     */
    public TypeMirror getEventType() {
        return eventType;
    }

    /**
     * @return Name of the handler method.
     */
    public Name getMethodName() {
        return methodName;
    }

    /**
     * @return Indicates the handler method's parameters.
     */
    public HandleEventMethodParams getMethodParams() {
        return methodParams;
    }

    /**
     * @return The handler method parameters.
     */
    public List<? extends VariableElement> getParameters() {
        return parameters;
    }

    /**
     * @return State type, defined by the handler method return type.
     */
    public TypeMirror getStateType() {
        return stateType;
    }

}
