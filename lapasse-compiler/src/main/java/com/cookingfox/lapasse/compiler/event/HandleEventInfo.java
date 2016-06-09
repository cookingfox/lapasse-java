package com.cookingfox.lapasse.compiler.event;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

/**
 * Created by abeldebeer on 09/06/16.
 */
public class HandleEventInfo extends AbstractHandleEvent {

    protected ExecutableElement executableElement;
    protected final HandleEventFirstParam firstParam;
    protected final HandleEventGeneral general;
    protected final HandleEventReturns returns;
    protected final HandleEventSecondParam secondParam;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleEventInfo(Element element) {
        super(element);

        firstParam = new HandleEventFirstParam(element);
        general = new HandleEventGeneral(element);
        returns = new HandleEventReturns(element);
        secondParam = new HandleEventSecondParam(element);
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

    public TypeName getEventName() {
        return secondParam.getParamName();
    }

    public Name getMethodName() {
        return executableElement.getSimpleName();
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
        return "HandleEventInfo{\n\t" +
                "general=" + general +
                ",\n\tfirstParam=" + firstParam +
                ",\n\tsecondParam=" + secondParam +
                ",\n\treturns=" + returns +
                "\n}";
    }

}
