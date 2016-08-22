package com.cookingfox.lapasse.compiler.processor.event;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.processor.ProcessorHelper;
import com.cookingfox.lapasse.compiler.utils.TypeUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static com.cookingfox.lapasse.compiler.processor.event.HandleEventAnnotationParams.ANNOTATION_NO_PARAMS;
import static com.cookingfox.lapasse.compiler.processor.event.HandleEventAnnotationParams.ANNOTATION_ONE_PARAM_EVENT;
import static com.cookingfox.lapasse.compiler.processor.event.HandleEventMethodParams.*;
import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;

/**
 * Processes a {@link HandleEvent} annotated handler method.
 */
public class HandleEventProcessor {

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

    public HandleEventResult process() throws Exception {
        checkMethod();

        ExecutableElement method = (ExecutableElement) element;
        HandleEvent annotation = method.getAnnotation(HandleEvent.class);
        List<? extends VariableElement> parameters = method.getParameters();
        TypeMirror returnType = method.getReturnType();

        result.annotationParams = determineAnnotationParams(annotation);
        result.methodParams = determineMethodParams(parameters);

        checkReturnType(returnType);

        result.methodName = element.getSimpleName();
        result.parameters = parameters;
        result.stateType = returnType;
        result.eventType = determineEventType();

        return result;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    protected void checkMethod() throws Exception {
        if (!ProcessorHelper.isAccessible(element)) {
            throw new Exception("Method is not accessible - it must be a non-static method with " +
                    "public, protected or package-level access");
        }
    }

    protected TypeMirror checkReturnType(TypeMirror returnType) throws Exception {
        if (!isSubtype(returnType, State.class)) {
            throw new Exception("Return type of @HandleEvent annotated method must extend " + State.class.getName());
        }

        return returnType;
    }

    protected Exception createInvalidMethodParamsException(List<? extends VariableElement> parameters) {
        return new Exception("Invalid parameters - expected event and state");
    }

    protected HandleEventAnnotationParams determineAnnotationParams(HandleEvent annotation) throws Exception {
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
        switch (result.methodParams) {
            case METHOD_ONE_PARAM_EVENT:
            case METHOD_TWO_PARAMS_EVENT_STATE:
                // first param
                return result.parameters.get(0).asType();

            case METHOD_TWO_PARAMS_STATE_EVENT:
                // second param
                return result.parameters.get(1).asType();
        }

        if (result.annotationParams == ANNOTATION_ONE_PARAM_EVENT) {
            return result.getAnnotationEventType();
        }

        throw new Exception(String.format("Could not determine the target event type. Add an " +
                "event method parameter or add the type to the annotation: " +
                "`@%s(event = MyEvent.class)`", HandleEvent.class.getSimpleName()));
    }

    protected HandleEventMethodParams determineMethodParams(List<? extends VariableElement> parameters) throws Exception {
        int numParams = parameters.size();

        if (numParams < 1) {
            return METHOD_NO_PARAMS;
        } else if (numParams > 2) {
            throw createInvalidMethodParamsException(parameters);
        }

        VariableElement firstParam = parameters.get(0);
        boolean firstIsEvent = isSubtype(firstParam, Event.class);
        boolean firstIsState = isSubtype(firstParam, State.class);

        if (!firstIsEvent && !firstIsState) {
            throw createInvalidMethodParamsException(parameters);
        }

        if (numParams == 1) {
            return firstIsEvent ? METHOD_ONE_PARAM_EVENT : METHOD_ONE_PARAM_STATE;
        }

        VariableElement secondParam = parameters.get(1);

        if (firstIsEvent && isSubtype(secondParam, State.class)) {
            return METHOD_TWO_PARAMS_EVENT_STATE;
        } else if (firstIsState && isSubtype(secondParam, Event.class)) {
            return METHOD_TWO_PARAMS_STATE_EVENT;
        }

        throw createInvalidMethodParamsException(parameters);
    }

}
