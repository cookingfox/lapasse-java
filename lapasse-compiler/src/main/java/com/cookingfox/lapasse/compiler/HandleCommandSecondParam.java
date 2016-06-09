package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.api.command.Command;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Created by abeldebeer on 09/06/16.
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

    private boolean validateParamExists() {
        List<? extends VariableElement> parameters = executableElement.getParameters();

        if (parameters.size() > 1) {
            secondParam = parameters.get(1).asType();

            return true;
        }

        return false;
    }

    private boolean validateParamExtendsCommand() {
        return TypeUtils.isSubtype(secondParam, Command.class);
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
