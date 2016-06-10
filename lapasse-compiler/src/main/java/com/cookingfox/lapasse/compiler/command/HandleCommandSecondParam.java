package com.cookingfox.lapasse.compiler.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.api.command.Command;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;

/**
 * Processes the second parameter of a {@link HandleCommand} annotated method.
 */
public class HandleCommandSecondParam extends AbstractHandleCommand {

    protected ExecutableElement executableElement;
    protected TypeMirror secondParam;

    protected boolean exists = false;
    protected boolean extendsCommand = false;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleCommandSecondParam(Element element) {
        super(element);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public String getError() {
        String prefix = String.format("Second parameter of @%s annotated method", ANNOTATION);

        if (isValid()) {
            return String.format("%s is valid", prefix);
        }

        if (!exists || !extendsCommand) {
            return String.format("%s must be a subtype of `%s`", prefix, Command.class.getName());
        }

        return String.format("%s is invalid", prefix);
    }

    @Override
    public boolean isValid() {
        return exists && extendsCommand;
    }

    public TypeName getParamName() {
        return TypeName.get(secondParam);
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
            extendsCommand = validateParamExtendsCommand();
        }
    }

    protected boolean validateParamExists() {
        List<? extends VariableElement> parameters = executableElement.getParameters();

        if (parameters.size() > 1) {
            secondParam = parameters.get(1).asType();

            return true;
        }

        return false;
    }

    protected boolean validateParamExtendsCommand() {
        return isSubtype(secondParam, Command.class);
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HandleCommandSecondParam{" +
                "exists=" + exists +
                ", extendsCommand=" + extendsCommand +
                '}';
    }

}
