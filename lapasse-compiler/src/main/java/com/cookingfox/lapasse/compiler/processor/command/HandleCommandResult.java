package com.cookingfox.lapasse.compiler.processor.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Result of a processed {@link HandleCommand} annotated handler method.
 */
public class HandleCommandResult {

    protected TypeMirror annotationCommandType;
    protected TypeMirror annotationStateType;
    protected HandleCommandAnnotationParams annotationParams;
    protected TypeMirror commandType;
    protected TypeMirror eventType;
    protected Name methodName;
    protected HandleCommandMethodParams methodParams;
    protected List<? extends VariableElement> parameters;
    protected HandleCommandReturnType returnType;
    protected TypeMirror returnTypeName;
    protected TypeMirror stateType;

    //----------------------------------------------------------------------------------------------
    // GETTERS
    //----------------------------------------------------------------------------------------------

    public TypeMirror getAnnotationCommandType() {
        return annotationCommandType;
    }

    public TypeMirror getAnnotationStateType() {
        return annotationStateType;
    }

    public HandleCommandAnnotationParams getAnnotationParams() {
        return annotationParams;
    }

    public TypeMirror getCommandType() {
        return commandType;
    }

    public TypeMirror getEventType() {
        return eventType;
    }

    public TypeName getEventTypeName() {
        if (getReturnType() == HandleCommandReturnType.RETURNS_VOID) {
            return TypeName.VOID;
        }

        return ClassName.get(getEventType());
    }

    public Name getMethodName() {
        return methodName;
    }

    public HandleCommandMethodParams getMethodParams() {
        return methodParams;
    }

    public List<? extends VariableElement> getParameters() {
        return parameters;
    }

    public HandleCommandReturnType getReturnType() {
        return returnType;
    }

    public TypeMirror getReturnTypeName() {
        return returnTypeName;
    }

    public TypeMirror getStateType() {
        return stateType;
    }

}
