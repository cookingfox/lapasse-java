package com.cookingfox.lapasse.compiler.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

/**
 * Provides information about a {@link HandleCommand} annotated element.
 */
public class HandleCommandInfo extends AbstractHandleCommand {

    protected ExecutableElement executableElement;
    protected final HandleCommandFirstParam firstParam;
    protected final HandleCommandGeneral general;
    protected final HandleCommandReturnType returns;
    protected final HandleCommandSecondParam secondParam;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleCommandInfo(Element element) {
        super(element);

        firstParam = new HandleCommandFirstParam(element);
        general = new HandleCommandGeneral(element);
        returns = new HandleCommandReturnType(element);
        secondParam = new HandleCommandSecondParam(element);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public String getError() {
        if (!general.isValid()) {
            return general.getError();
        } else if (!firstParam.isValid()) {
            return firstParam.getError();
        } else if (!secondParam.isValid()) {
            return secondParam.getError();
        } else if (!returns.isValid()) {
            return returns.getError();
        }

        return "No error";
    }

    public TypeName getCommandName() {
        return secondParam.getParamName();
    }

    /**
     * WARNING: this will throw if the return type is void.
     *
     * @return The event name.
     */
    public TypeName getEventName() {
        return returns.getEventName();
    }

    public Name getMethodName() {
        return executableElement.getSimpleName();
    }

    public TypeName getMethodReturnTypeName() {
        return returns.getMethodReturnTypeName();
    }

    public TypeName getStateName() {
        return firstParam.getParamName();
    }

    @Override
    public boolean isValid() {
        return general.isValid() &&
                firstParam.isValid() &&
                secondParam.isValid() &&
                returns.isValid();
    }

    public boolean returnsEventCallable() {
        return returns.returnsEventCallable();
    }

    public boolean returnsEventCollection() {
        return returns.returnsEventCollection();
    }

    public boolean returnsEventCollectionCallable() {
        return returns.returnsEventCollectionCallable();
    }

    public boolean returnsVoid() {
        return returns.returnsVoid();
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void doProcess() {
        general.process();

        if (general.isValid()) {
            // get and set executable element
            executableElement = general.getExecutableElement();
            firstParam.setExecutableElement(executableElement);
            secondParam.setExecutableElement(executableElement);
            returns.setExecutableElement(executableElement);

            firstParam.process();
        }

        if (firstParam.isValid()) {
            secondParam.process();
        }

        if (secondParam.isValid()) {
            returns.process();
        }
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HandleCommandInfo{\n\t" +
                "general=" + general +
                ",\n\tfirstParam=" + firstParam +
                ",\n\tsecondParam=" + secondParam +
                ",\n\treturns=" + returns +
                "\n}";
    }

}
