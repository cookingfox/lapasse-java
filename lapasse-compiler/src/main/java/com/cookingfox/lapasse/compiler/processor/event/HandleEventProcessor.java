package com.cookingfox.lapasse.compiler.processor.event;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.utils.TypeUtils;

import javax.lang.model.element.*;
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
                Modifier.STATIC,
                Modifier.STRICTFP,
                Modifier.VOLATILE
        ));
    }

    protected final Element element;
    protected HandleEventAnnotationType annotationType;
    protected HandleEventMethodType methodType;

    public HandleEventProcessor(Element element) {
        this.element = element;
    }

    public void process() throws Exception {
        checkMethod();

        ExecutableElement method = (ExecutableElement) this.element;
        HandleEvent annotation = method.getAnnotation(HandleEvent.class);
        List<? extends VariableElement> parameters = method.getParameters();
        TypeMirror returnType = method.getReturnType();

        annotationType = determineAnnotationType(annotation);
        methodType = determineMethodType(parameters);

        checkAnnotationAndMethodType();

        checkReturnType(returnType);
    }

    protected void checkAnnotationAndMethodType() throws Exception {
        if (methodType == METHOD_NO_PARAMS && annotationType == ANNOTATION_NO_PARAMS) {
            throw new Exception("Method has no params, so annotation should set event type");
        }
    }

    protected void checkMethod() throws Exception {
        if (element.getKind() != ElementKind.METHOD) {
            throw new Exception("Annotated element must be a method");
        }

        if (Collections.disjoint(element.getModifiers(), MODIFIERS)) {
            throw new Exception("Method is not accessible");
        }
    }

    protected void checkReturnType(TypeMirror returnType) throws Exception {
        if (!isSubtype(returnType, State.class)) {
            throw new Exception("Return type of @HandleEvent annotated method must extend " + State.class.getName());
        }
    }

    protected HandleEventAnnotationType determineAnnotationType(HandleEvent annotation) throws Exception {
        try {
            annotation.event(); // this should throw
        } catch (MirroredTypeException e) {
            if (TypeUtils.equalsType(e.getTypeMirror(), HandleEvent.EmptyEvent.class)) {
                return ANNOTATION_NO_PARAMS;
            }

            return ANNOTATION_ONE_PARAM_EVENT;
        }

        throw new Exception("Expected exception");
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
