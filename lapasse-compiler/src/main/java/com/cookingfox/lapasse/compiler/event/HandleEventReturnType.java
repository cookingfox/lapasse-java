package com.cookingfox.lapasse.compiler.event;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.state.State;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;

/**
 * Processes the return type of a {@link HandleEvent} annotated method.
 */
public class HandleEventReturnType extends AbstractHandleEvent {

    protected ExecutableElement executableElement;
    protected DeclaredType returnType;

    protected boolean isDeclaredType = false;
    protected boolean returnsState = false;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleEventReturnType(Element element) {
        super(element);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public String getError() {
        String prefix = String.format("Return type of @%s annotated method", ANNOTATION);

        if (isValid()) {
            return String.format("%s is valid", prefix);
        }

        if (!isDeclaredType || !returnsState) {
            return String.format("%s must extend `%s`", prefix, State.class.getName());
        }

        return String.format("%s is invalid", prefix);
    }

    @Override
    public boolean isValid() {
        return isDeclaredType && returnsState;
    }

    public void setExecutableElement(ExecutableElement executableElement) {
        this.executableElement = executableElement;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void doProcess() {
        if (isDeclaredType = validateDeclaredType()) {
            returnsState = validateReturnsState();
        }
    }

    protected boolean validateReturnsState() {
        return isSubtype(returnType, State.class);
    }

    protected boolean validateDeclaredType() {
        TypeMirror mirrorReturnType = executableElement.getReturnType();

        if (mirrorReturnType.getKind() != TypeKind.DECLARED) {
            return false;
        }

        returnType = (DeclaredType) mirrorReturnType;

        return true;
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HandleEventReturnType{" +
                "isDeclaredType=" + isDeclaredType +
                ", returnsState=" + returnsState +
                '}';
    }

}
