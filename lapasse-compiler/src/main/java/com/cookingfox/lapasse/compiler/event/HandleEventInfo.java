package com.cookingfox.lapasse.compiler.event;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.compiler.base.HandleInfo;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

/**
 * Provides information about a {@link HandleEvent} annotated element.
 */
public class HandleEventInfo extends AbstractHandleEvent implements HandleInfo {

    protected ExecutableElement executableElement;
    protected final HandleEventFirstParam firstParam;
    protected final HandleEventGeneral general;
    protected final HandleEventReturnType returns;
    protected final HandleEventSecondParam secondParam;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleEventInfo(Element element) {
        super(element);

        returns = new HandleEventReturnType(element);
        firstParam = new HandleEventFirstParam(element);
        general = new HandleEventGeneral(element);
        secondParam = new HandleEventSecondParam(element);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public String getError() {
        if (!general.isValid()) {
            return general.getError();
        } else if (!returns.isValid()) {
            return returns.getError();
        } else if (!firstParam.isValid()) {
            return firstParam.getError();
        } else if (!secondParam.isValid()) {
            return secondParam.getError();
        }

        return "No error";
    }

    public TypeName getEventName() {
        return secondParam.getParamName();
    }

    public Name getMethodName() {
        return executableElement.getSimpleName();
    }

    @Override
    public TypeName getStateName() {
        return returns.getTypeName();
    }

    @Override
    public boolean isValid() {
        return general.isValid() &&
                returns.isValid() &&
                firstParam.isValid() &&
                secondParam.isValid();
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
            returns.setExecutableElement(executableElement);
            firstParam.setExecutableElement(executableElement);
            secondParam.setExecutableElement(executableElement);

            returns.process();
        }

        if (returns.isValid()) {
            firstParam.process();
        }

        if (firstParam.isValid()) {
            secondParam.process();
        }
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HandleEventInfo{\n\t" +
                "general=" + general +
                ",\n\treturns=" + returns +
                ",\n\tfirstParam=" + firstParam +
                ",\n\tsecondParam=" + secondParam +
                "\n}";
    }

}
