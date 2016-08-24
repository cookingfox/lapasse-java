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
import javax.lang.model.util.Types;
import java.util.LinkedList;
import java.util.List;

import static com.cookingfox.lapasse.compiler.processor.event.HandleEventAnnotationParams.ANNOTATION_NO_PARAMS;
import static com.cookingfox.lapasse.compiler.processor.event.HandleEventAnnotationParams.ANNOTATION_ONE_PARAM_EVENT;
import static com.cookingfox.lapasse.compiler.processor.event.HandleEventMethodParams.*;
import static com.cookingfox.lapasse.compiler.utils.TypeUtils.equalsType;
import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;

/**
 * Processes a {@link HandleEvent} annotated handler method.
 */
public class HandleEventProcessor {

    /**
     * The annotated element.
     */
    protected final Element element;

    /**
     * Value Object containing the processing results.
     */
    protected final HandleEventResult result = new HandleEventResult();

    /**
     * Utility methods for operating on types.
     */
    protected final Types types;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleEventProcessor(Element element, Types types) {
        this.element = element;
        this.types = types;
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Process the {@link HandleEvent} annotated handler method and create a result object with the
     * extracted values.
     *
     * @return The result object of process operation.
     * @throws Exception when the handler method is invalid.
     */
    public HandleEventResult process() throws Exception {
        ExecutableElement method = ProcessorHelper.validateAndGetAnnotatedMethod(element);
        HandleEvent eventAnnotation = method.getAnnotation(HandleEvent.class);
        List<? extends VariableElement> parameters = method.getParameters();
        TypeMirror returnType = method.getReturnType();

        // populate result
        result.annotationParams = determineAnnotationParams(eventAnnotation);
        result.methodParams = determineMethodParams(parameters);
        result.methodName = element.getSimpleName();
        result.parameters = parameters;
        result.stateType = determineStateType(returnType);
        result.eventType = determineEventType();

        detectTypesConflict();

        return result;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates an exception for when the handler method's parameters are invalid.
     *
     * @param parameters The handler method's parameters.
     * @return The exception with the formatted error message.
     */
    protected Exception createInvalidMethodParamsException(List<? extends VariableElement> parameters) {
        List<TypeMirror> types = new LinkedList<>();

        for (VariableElement parameter : parameters) {
            types.add(parameter.asType());
        }

        return new Exception(String.format("Method parameters are invalid (expected State and " +
                "Event implementations): %s", types));
    }

    /**
     * Checks whether there is a conflict between the types defined by the annotation, the method's
     * parameters and the method's return type.
     *
     * @throws Exception when there is a conflict between the defined types.
     */
    protected void detectTypesConflict() throws Exception {
        TypeMirror annotationEventType = result.getAnnotationEventType();
        TypeMirror eventType = result.getEventType();
        TypeMirror methodParamStateType = result.getMethodParamStateType();
        TypeMirror stateType = result.getStateType();

        // compare annotation and method event type
        if (annotationEventType != null && !types.isSameType(eventType, annotationEventType)) {
            throw new Exception(String.format("Annotation parameter for event (`%s`) has " +
                    "different type than method parameter (`%s`)", annotationEventType, eventType));
        }

        // compare method and return value state type
        if (methodParamStateType != null && !types.isSameType(methodParamStateType, stateType)) {
            throw new Exception(String.format("Method parameter for state (`%s`) has different " +
                    "type than return type (`%s`)", methodParamStateType, stateType));
        }
    }

    /**
     * Determines the type of parameters for the {@link HandleEvent} annotation. The annotation
     * can hold a reference to the concrete event class that this method should handle.
     *
     * @param annotation The annotation object.
     * @return An enum which indicates the annotation parameters.
     * @throws Exception when the annotation parameters could not be determined.
     */
    protected HandleEventAnnotationParams determineAnnotationParams(HandleEvent annotation) throws Exception {
        TypeMirror annotationEventType = null;

        try {
            annotation.event(); // this should throw
        } catch (MirroredTypeException e) {
            annotationEventType = e.getTypeMirror();
        }

        // this should never happen
        if (annotationEventType == null) {
            throw new Exception("Could not extract event type from annotation");
        }

        // should not be base type
        if (TypeUtils.equalsType(annotationEventType, Event.class)) {
            return ANNOTATION_NO_PARAMS;
        }

        result.annotationEventType = annotationEventType;

        return ANNOTATION_ONE_PARAM_EVENT;
    }

    /**
     * Determines the concrete event type of the handler method. The event type can be set by both
     * the method parameters (event object) or the annotation (event class).
     *
     * @return The concrete event type of the handler method.
     * @throws Exception when the concrete event type could not be determined.
     */
    protected TypeMirror determineEventType() throws Exception {
        TypeMirror eventType = null;

        // determine with method params
        switch (result.getMethodParams()) {
            case METHOD_ONE_PARAM_EVENT:
            case METHOD_TWO_PARAMS_EVENT_STATE:
                // first param
                eventType = result.getParameters().get(0).asType();
                break;

            case METHOD_TWO_PARAMS_STATE_EVENT:
                // second param
                eventType = result.getParameters().get(1).asType();
                break;
        }

        // determine with annotation params
        if (eventType == null && result.getAnnotationParams() == ANNOTATION_ONE_PARAM_EVENT) {
            eventType = result.getAnnotationEventType();
        }

        // no event type: throw
        if (eventType == null) {
            throw new Exception(String.format("Could not determine the target event type. Add an " +
                    "event method parameter or add the type to the annotation: " +
                    "`@%s(event = MyEvent.class)`", HandleEvent.class.getSimpleName()));
        }

        // validate event
        return extendsEvent(eventType);
    }

    /**
     * Validates and identifies the handler method parameters.
     *
     * @param parameters The method parameters.
     * @return An indication of the method parameters.
     * @throws Exception when the method parameters are invalid.
     */
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
            if (firstIsEvent) {
                return METHOD_ONE_PARAM_EVENT;
            }

            result.methodParamStateType = extendsState(firstParam.asType());

            return METHOD_ONE_PARAM_STATE;
        }

        VariableElement secondParam = parameters.get(1);

        if (firstIsEvent && isSubtype(secondParam, State.class)) {
            result.methodParamStateType = extendsState(secondParam.asType());

            return METHOD_TWO_PARAMS_EVENT_STATE;
        } else if (firstIsState && isSubtype(secondParam, Event.class)) {
            result.methodParamStateType = extendsState(firstParam.asType());

            return METHOD_TWO_PARAMS_STATE_EVENT;
        }

        throw createInvalidMethodParamsException(parameters);
    }

    /**
     * Determines the concrete state type based on the handler method's return type.
     *
     * @param returnType The return type of the handler method
     * @return The concrete state type of the handler method.
     * @throws Exception when the return type is an invalid state type.
     */
    protected TypeMirror determineStateType(TypeMirror returnType) throws Exception {
        if (!isSubtype(returnType, State.class) || equalsType(returnType, State.class)) {
            throw new Exception("Return type of @HandleEvent annotated method must extend "
                    + State.class.getName());
        }

        return returnType;
    }

    /**
     * Asserts the provided type extends event.
     *
     * @param type The type to validate.
     * @return The provided type, if valid.
     * @throws Exception when the provided is equal to the event base type.
     */
    protected TypeMirror extendsEvent(TypeMirror type) throws Exception {
        if (equalsType(type, Event.class)) {
            throw new Exception(String.format("Event parameter cannot be the base type `%s`",
                    Event.class.getName()));
        }

        return type;
    }

    /**
     * Asserts the provided type extends state.
     *
     * @param type The type to validate.
     * @return The provided type, if valid.
     * @throws Exception when the provided is equal to the state base type.
     */
    protected TypeMirror extendsState(TypeMirror type) throws Exception {
        if (equalsType(type, State.class)) {
            throw new Exception(String.format("State parameter cannot be the base type `%s`",
                    State.class.getName()));
        }

        return type;
    }

}
