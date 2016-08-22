package com.cookingfox.lapasse.compiler.processor.event;

import com.cookingfox.lapasse.annotation.HandleEvent;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Result of a processed {@link HandleEvent} annotated handler method.
 */
public class HandleEventResult {

    protected TypeMirror annotationEventType;
    protected HandleEventAnnotationParams annotationParams;
    protected TypeMirror eventType;
    protected Name methodName;
    protected HandleEventMethodParams methodParams;
    protected List<? extends VariableElement> parameters;
    protected TypeMirror stateType;

    //----------------------------------------------------------------------------------------------
    // GETTERS
    //----------------------------------------------------------------------------------------------

    public TypeMirror getAnnotationEventType() {
        return annotationEventType;
    }

    public HandleEventAnnotationParams getAnnotationParams() {
        return annotationParams;
    }

    public TypeMirror getEventType() {
        return eventType;
    }

    public Name getMethodName() {
        return methodName;
    }

    public HandleEventMethodParams getMethodParams() {
        return methodParams;
    }

    public List<? extends VariableElement> getParameters() {
        return parameters;
    }

    public TypeMirror getStateType() {
        return stateType;
    }

}
