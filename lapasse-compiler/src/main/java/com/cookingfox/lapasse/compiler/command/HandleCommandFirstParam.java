package com.cookingfox.lapasse.compiler.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.api.state.State;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;

/**
 * Processes the first parameter of a {@link HandleCommand} annotated method.
 */
public class HandleCommandFirstParam extends AbstractHandleCommand {

    protected ExecutableElement executableElement;
    protected TypeMirror firstParam;

    protected boolean exists = false;
    protected boolean extendsState = false;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleCommandFirstParam(Element element) {
        super(element);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public String getError() {
        String prefix = String.format("First parameter of @%s annotated method", ANNOTATION);

        if (isValid()) {
            return String.format("%s is valid", prefix);
        }

        if (!exists || !extendsState) {
            return String.format("%s must be a subtype of `%s`", prefix, State.class.getName());
        }

        return String.format("%s is invalid", prefix);
    }

    @Override
    public boolean isValid() {
        return exists && extendsState;
    }

    public TypeName getParamName() {
        return TypeName.get(firstParam);
    }

    public void setExecutableElement(ExecutableElement executableElement) {
        this.executableElement = executableElement;
    }
    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void doProcess() {
        if (exists = validateParamExists()) {
            extendsState = validateParamExtendsState();
        }
    }

    protected boolean validateParamExists() {
        List<? extends VariableElement> parameters = executableElement.getParameters();

        if (parameters.size() > 0) {
            firstParam = parameters.get(0).asType();

            return true;
        }

        return false;
    }

    protected boolean validateParamExtendsState() {
        return isSubtype(firstParam, State.class);
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HandleCommandFirstParam{" +
                "exists=" + exists +
                ", extendsState=" + extendsState +
                '}';
    }

}
