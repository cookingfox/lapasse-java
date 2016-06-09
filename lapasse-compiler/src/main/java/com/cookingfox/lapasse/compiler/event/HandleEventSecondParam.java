package com.cookingfox.lapasse.compiler.event;

import com.cookingfox.lapasse.api.event.Event;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;

/**
 * Created by abeldebeer on 09/06/16.
 */
public class HandleEventSecondParam extends AbstractHandleEvent {

    protected ExecutableElement executableElement;
    protected TypeMirror secondParam;

    protected boolean exists = false;
    protected boolean extendsEvent = false;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleEventSecondParam(Element element) {
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

        if (!exists || !extendsEvent) {
            return String.format("%s must be a subtype of `%s`", prefix, Event.class.getName());
        }

        return String.format("%s is invalid", prefix);
    }

    @Override
    public boolean isValid() {
        return exists && extendsEvent;
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
            extendsEvent = validateParamExtendsEvent();
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

    private boolean validateParamExtendsEvent() {
        return isSubtype(secondParam, Event.class);
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HandleEventSecondParam{" +
                "exists=" + exists +
                ", extendsEvent=" + extendsEvent +
                '}';
    }

}
