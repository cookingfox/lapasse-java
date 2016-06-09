package com.cookingfox.lapasse.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * Created by abeldebeer on 09/06/16.
 */
public class HandleCommandInfo extends AbstractHandleCommand {

    protected final HandleCommandFirstParam firstParam;
    protected final HandleCommandGeneral general;
    protected final HandleCommandReturns returns;
    protected final HandleCommandSecondParam secondParam;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleCommandInfo(Element element) {
        super(element);

        general = new HandleCommandGeneral(element);
        firstParam = new HandleCommandFirstParam(element);
        secondParam = new HandleCommandSecondParam(element);
        returns = new HandleCommandReturns(element);
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

    public HandleCommandFirstParam getFirstParam() {
        return firstParam;
    }

    public HandleCommandGeneral getGeneral() {
        return general;
    }

    public HandleCommandReturns getReturns() {
        return returns;
    }

    public HandleCommandSecondParam getSecondParam() {
        return secondParam;
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
            ExecutableElement executableElement = general.getExecutableElement();
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
