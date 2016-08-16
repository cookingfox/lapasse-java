package com.cookingfox.lapasse.compiler.processor.event;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.utils.TypeUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cookingfox.lapasse.compiler.processor.event.HandleEventAnnotationType.ANNOTATION_NO_PARAMS;
import static com.cookingfox.lapasse.compiler.processor.event.HandleEventAnnotationType.ANNOTATION_ONE_PARAM_EVENT;
import static com.cookingfox.lapasse.compiler.processor.event.HandleEventMethodType.*;
import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;

/**
 * Created by abeldebeer on 15/08/16.
 */
public class HandleEventProcessor {

    protected static final List<Modifier> MODIFIERS;

    static {
        MODIFIERS = Collections.unmodifiableList(Arrays.asList(
                Modifier.ABSTRACT,
                Modifier.NATIVE,
                Modifier.PRIVATE,
                Modifier.STATIC,
                Modifier.STRICTFP,
                Modifier.VOLATILE
        ));
    }

    protected final Element element;
    protected final HandleEventResult result = new HandleEventResult();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleEventProcessor(Element element) {
        this.element = element;
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    public HandleEventResult getResult() {
        return result;
    }

    public void process() throws Exception {
        checkMethod();

        ExecutableElement method = (ExecutableElement) element;
        HandleEvent annotation = method.getAnnotation(HandleEvent.class);
        List<? extends VariableElement> parameters = method.getParameters();
        TypeMirror returnType = method.getReturnType();

        result.annotationType = determineAnnotationType(annotation);
        result.methodType = determineMethodType(parameters);

        checkAnnotationAndMethodType();
        checkReturnType(returnType);

        result.methodName = element.getSimpleName();
        result.parameters = parameters;
        result.stateType = returnType;
        result.eventType = determineEventType();
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    protected void checkAnnotationAndMethodType() throws Exception {
        if (result.methodType == METHOD_NO_PARAMS && result.annotationType == ANNOTATION_NO_PARAMS) {
            throw new Exception("Method has no params, so annotation should set event type");
        }
    }

    protected void checkMethod() throws Exception {
        if (!Collections.disjoint(element.getModifiers(), MODIFIERS)) {
            throw new Exception("Method is not accessible");
        }
    }

    protected TypeMirror checkReturnType(TypeMirror returnType) throws Exception {
        if (!isSubtype(returnType, State.class)) {
            throw new Exception("Return type of @HandleEvent annotated method must extend " + State.class.getName());
        }

        return returnType;
    }

    protected HandleEventAnnotationType determineAnnotationType(HandleEvent annotation) throws Exception {
        try {
            annotation.event(); // this should throw
        } catch (MirroredTypeException e) {
            TypeMirror annotationEventType = e.getTypeMirror();

            if (TypeUtils.equalsType(annotationEventType, HandleEvent.EmptyEvent.class)) {
                return ANNOTATION_NO_PARAMS;
            }

            result.annotationEventType = annotationEventType;

            return ANNOTATION_ONE_PARAM_EVENT;
        }

        // we should never get here
        throw new Exception("Could not determine annotation type");
    }

    protected TypeMirror determineEventType() throws Exception {
        switch (result.methodType) {
            case METHOD_ONE_PARAM_EVENT:
            case METHOD_TWO_PARAMS_EVENT_STATE:
                // first param
                return result.parameters.get(0).asType();

            case METHOD_TWO_PARAMS_STATE_EVENT:
                // second param
                return result.parameters.get(1).asType();
        }

        if (result.annotationType == ANNOTATION_ONE_PARAM_EVENT) {
            return result.getAnnotationEventType();
        }

        throw new Exception("Could not determine event type");
    }

    protected HandleEventMethodType determineMethodType(List<? extends VariableElement> parameters) throws Exception {
        int numParams = parameters.size();

        if (numParams < 1) {
            return METHOD_NO_PARAMS;
        } else if (numParams > 2) {
            throw new Exception("Invalid number of parameters");
        }

        VariableElement firstParam = parameters.get(0);
        boolean firstIsEvent = isSubtype(firstParam, Event.class);

        if (numParams == 1) {
            if (firstIsEvent) {
                return METHOD_ONE_PARAM_EVENT;
            }

            throw new Exception("Single parameter must be event");
        }

        VariableElement secondParam = parameters.get(1);

        if (firstIsEvent && isSubtype(secondParam, State.class)) {
            return METHOD_TWO_PARAMS_EVENT_STATE;
        } else if (isSubtype(firstParam, State.class) && isSubtype(secondParam, Event.class)) {
            return METHOD_TWO_PARAMS_STATE_EVENT;
        }

        throw new Exception("Invalid parameters - expected event and state");
    }


}
