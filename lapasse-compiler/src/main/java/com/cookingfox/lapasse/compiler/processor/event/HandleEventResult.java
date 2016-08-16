package com.cookingfox.lapasse.compiler.processor.event;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Created by abeldebeer on 15/08/16.
 */
public class HandleEventResult {

    protected TypeMirror annotationEventType;
    protected HandleEventAnnotationType annotationType;
    protected TypeMirror eventType;
    protected Name methodName;
    protected HandleEventMethodType methodType;
    protected List<? extends VariableElement> parameters;
    protected TypeMirror stateType;

    //----------------------------------------------------------------------------------------------
    // GETTERS
    //----------------------------------------------------------------------------------------------

    public TypeMirror getAnnotationEventType() {
        return annotationEventType;
    }

    public HandleEventAnnotationType getAnnotationType() {
        return annotationType;
    }

    public TypeMirror getEventType() {
        return eventType;
    }

    public Name getMethodName() {
        return methodName;
    }

    public HandleEventMethodType getMethodType() {
        return methodType;
    }

    public List<? extends VariableElement> getParameters() {
        return parameters;
    }

    public TypeMirror getStateType() {
        return stateType;
    }

}
