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

    /**
     * Command type, defined by the annotation.
     */
    protected TypeMirror annotationCommandType;

    /**
     * State type, defined by the annotation.
     */
    protected TypeMirror annotationStateType;

    /**
     * Indicates the annotation's parameters.
     */
    protected HandleCommandAnnotationParams annotationParams;

    /**
     * Command type, defined by the handler method parameters.
     */
    protected TypeMirror commandType;

    /**
     * Event type, defined by the handler method return type.
     */
    protected TypeMirror eventType;

    /**
     * Name of the handler method.
     */
    protected Name methodName;

    /**
     * Indicates the handler method's parameters.
     */
    protected HandleCommandMethodParams methodParams;

    /**
     * The handler method parameters.
     */
    protected List<? extends VariableElement> parameters;

    /**
     * Return type of the handler method.
     */
    protected TypeMirror returnType;

    /**
     * Indicates the return value of the handler method.
     */
    protected HandleCommandReturnValue returnValue;

    /**
     * State type, defined by the handler method parameters.
     */
    protected TypeMirror stateType;

    //----------------------------------------------------------------------------------------------
    // GETTERS
    //----------------------------------------------------------------------------------------------

    /**
     * @return Command type, defined by the annotation.
     */
    public TypeMirror getAnnotationCommandType() {
        return annotationCommandType;
    }

    /**
     * @return State type, defined by the annotation.
     */
    public TypeMirror getAnnotationStateType() {
        return annotationStateType;
    }

    /**
     * @return Indicates the annotation's parameters.
     */
    public HandleCommandAnnotationParams getAnnotationParams() {
        return annotationParams;
    }

    /**
     * @return Command type, defined by the handler method parameters.
     */
    public TypeMirror getCommandType() {
        return commandType;
    }

    /**
     * @return The class name for the command.
     */
    public TypeName getCommandTypeName() {
        return ClassName.get(getCommandType());
    }

    /**
     * @return Event type, defined by the handler method return type.
     */
    public TypeMirror getEventType() {
        return eventType;
    }

    /**
     * Determines the event type name based on the return type.
     *
     * @return The event type name.
     */
    public TypeName getEventTypeName() {
        if (getReturnValue() == HandleCommandReturnValue.RETURNS_VOID) {
            return TypeName.VOID;
        }

        return ClassName.get(getEventType());
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
    public HandleCommandMethodParams getMethodParams() {
        return methodParams;
    }

    /**
     * @return The handler method parameters.
     */
    public List<? extends VariableElement> getParameters() {
        return parameters;
    }

    /**
     * @return Return type of the handler method.
     */
    public TypeMirror getReturnType() {
        return returnType;
    }

    /**
     * @return The class name for the return type.
     */
    public TypeName getReturnTypeName() {
        return ClassName.get(getReturnType());
    }

    /**
     * @return Indicates the return value of the handler method.
     */
    public HandleCommandReturnValue getReturnValue() {
        return returnValue;
    }

    /**
     * @return State type, defined by the handler method parameters.
     */
    public TypeMirror getStateType() {
        return stateType;
    }

}
