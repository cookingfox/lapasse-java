package com.cookingfox.lapasse.compiler.processor.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import rx.Observable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static com.cookingfox.lapasse.compiler.processor.command.HandleCommandAnnotationType.*;
import static com.cookingfox.lapasse.compiler.processor.command.HandleCommandMethodType.*;
import static com.cookingfox.lapasse.compiler.processor.command.HandleCommandReturnType.*;
import static com.cookingfox.lapasse.compiler.utils.TypeUtils.*;

/**
 * Created by abeldebeer on 16/08/16.
 */
public class HandleCommandProcessor {

    protected static final List<Modifier> MODIFIERS;

    static {
        MODIFIERS = Collections.unmodifiableList(Arrays.asList(
                Modifier.ABSTRACT,
                Modifier.NATIVE,
                Modifier.PROTECTED,
                Modifier.STATIC,
                Modifier.STRICTFP,
                Modifier.VOLATILE
        ));
    }

    protected final Element element;
    protected final HandleCommandResult result = new HandleCommandResult();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleCommandProcessor(Element element) {
        this.element = element;
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    public HandleCommandResult process() throws Exception {
        checkMethod();

        ExecutableElement method = (ExecutableElement) element;
        HandleCommand annotation = method.getAnnotation(HandleCommand.class);
        List<? extends VariableElement> parameters = method.getParameters();
        TypeMirror returnType = getReturnType(method.getReturnType());

        result.annotationType = determineAnnotationType(annotation);
        result.methodType = determineMethodType(parameters);
        result.returnType = determineReturnType(returnType);
        result.returnTypeName = returnType;

        checkAnnotationAndMethodType();

        // note: event type is set by `determineReturnType`

        result.methodName = element.getSimpleName();
        result.parameters = parameters;
        result.commandType = determineCommandType();
        result.stateType = determineStateType();

        return result;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    protected void checkAnnotationAndMethodType() throws Exception {
        if (result.methodType == METHOD_NO_PARAMS && result.annotationType == ANNOTATION_NO_PARAMS) {
            throw new Exception("Method has no params, so annotation should set command type");
        }
    }

    protected void checkMethod() throws Exception {
        if (!Collections.disjoint(element.getModifiers(), MODIFIERS)) {
            throw new Exception("Method is not accessible");
        }
    }

    protected HandleCommandAnnotationType determineAnnotationType(HandleCommand annotation) throws Exception {
        TypeMirror annotationCommandType = null;
        TypeMirror annotationStateType = null;

        try {
            annotation.command(); // this should throw
        } catch (MirroredTypeException e) {
            annotationCommandType = e.getTypeMirror();
        }

        try {
            annotation.state(); // this should throw
        } catch (MirroredTypeException e) {
            annotationStateType = e.getTypeMirror();
        }

        // this should never happen
        if (annotationCommandType == null || annotationStateType == null) {
            throw new Exception("Could not extract command or state type from annotation");
        }

        // should not be default "empty" type from annotation
        boolean hasCommand = !equalsType(annotationCommandType, HandleCommand.EmptyCommand.class);
        boolean hasState = !equalsType(annotationStateType, HandleCommand.EmptyState.class);

        if (hasCommand) {
            result.annotationCommandType = annotationCommandType;
        }
        if (hasState) {
            result.annotationStateType = annotationStateType;
        }

        if (hasCommand && hasState) {
            return ANNOTATION_TWO_PARAMS_COMMAND_STATE;
        } else if (hasCommand) {
            return ANNOTATION_ONE_PARAM_COMMAND;
        } else if (hasState) {
            return ANNOTATION_ONE_PARAM_STATE;
        }

        return ANNOTATION_NO_PARAMS;
    }

    protected TypeMirror determineCommandType() throws Exception {
        switch (result.getMethodType()) {
            case METHOD_ONE_PARAM_COMMAND:
            case METHOD_TWO_PARAMS_COMMAND_STATE:
                // first param
                return result.getParameters().get(0).asType();

            case METHOD_TWO_PARAMS_STATE_COMMAND:
                // second param
                return result.getParameters().get(1).asType();
        }

        switch (result.getAnnotationType()) {
            case ANNOTATION_ONE_PARAM_COMMAND:
            case ANNOTATION_TWO_PARAMS_COMMAND_STATE:
                return result.getAnnotationCommandType();
        }

        throw new Exception("Could not determine command type");
    }

    protected HandleCommandMethodType determineMethodType(List<? extends VariableElement> parameters) throws Exception {
        int numParams = parameters.size();

        if (numParams == 0) {
            return METHOD_NO_PARAMS;
        } else if (numParams > 2) {
            throw new Exception("Invalid number of parameters");
        }

        VariableElement firstParam = parameters.get(0);
        boolean firstIsCommand = isSubtype(firstParam, Command.class);
        boolean firstIsState = isSubtype(firstParam, State.class);

        if (!firstIsCommand && !firstIsState) {
            throw new Exception("Invalid parameters - expected command and state");
        }

        if (numParams == 1) {
            return firstIsCommand ? METHOD_ONE_PARAM_COMMAND : METHOD_ONE_PARAM_STATE;
        }

        VariableElement secondParam = parameters.get(1);

        if (firstIsCommand && isSubtype(secondParam, State.class)) {
            return METHOD_TWO_PARAMS_COMMAND_STATE;
        } else if (isSubtype(firstParam, State.class) && isSubtype(secondParam, Command.class)) {
            return METHOD_TWO_PARAMS_STATE_COMMAND;
        }

        throw new Exception("Invalid parameters - expected command and state");
    }

    protected HandleCommandReturnType determineReturnType(TypeMirror returnType) throws Exception {
        if (returnType.getKind() == TypeKind.VOID) {
            return RETURNS_VOID;
        } else if (isSubtype(returnType, Event.class)) {
            result.eventType = returnType;

            return RETURNS_EVENT;
        }

        boolean returnsCallable = isSubtype(returnType, Callable.class);
        boolean returnsCollection = isSubtype(returnType, Collection.class);
        boolean returnsObservable = isSubtype(returnType, Observable.class);

        if (!returnsCallable && !returnsCollection && !returnsObservable) {
            throw new Exception("Invalid return type");
        }

        List<? extends TypeMirror> typeArguments = ((DeclaredType) returnType).getTypeArguments();
        DeclaredType firstArg = (DeclaredType) typeArguments.get(0);

        if (isSubtype(firstArg, Event.class)) {
            result.eventType = firstArg;

            if (returnsCallable) {
                return RETURNS_EVENT_CALLABLE;
            } else if (returnsCollection) {
                return RETURNS_EVENT_COLLECTION;
            }

            return RETURNS_EVENT_OBSERVABLE;
        } else if (returnsCollection || !firstArgIsSubType(returnType, Collection.class)) {
            // throw: below expects callable or observable of collection
            throw new Exception("Invalid return type");
        }

        DeclaredType firstArgFirstArg = (DeclaredType) firstArg.getTypeArguments().get(0);

        // check whether the generic type of the callable / observable is `Collection<Event>`
        if (isSubtype(firstArgFirstArg, Event.class)) {
            result.eventType = firstArgFirstArg;

            if (returnsCallable) {
                return RETURNS_EVENT_COLLECTION_CALLABLE;
            } else if (returnsObservable) {
                return RETURNS_EVENT_COLLECTION_OBSERVABLE;
            }
        }

        throw new Exception("Invalid return type");
    }

    protected TypeMirror determineStateType() throws Exception {
        switch (result.getMethodType()) {
            case METHOD_ONE_PARAM_STATE:
            case METHOD_TWO_PARAMS_STATE_COMMAND:
                // first param
                return result.getParameters().get(0).asType();

            case METHOD_TWO_PARAMS_COMMAND_STATE:
                // second param
                return result.getParameters().get(1).asType();
        }

        switch (result.getAnnotationType()) {
            case ANNOTATION_ONE_PARAM_STATE:
            case ANNOTATION_TWO_PARAMS_COMMAND_STATE:
                return result.getAnnotationStateType();
        }

        return null;
    }

    protected TypeMirror getReturnType(TypeMirror returnType) throws Exception {
        if (returnType.getKind() != TypeKind.VOID && returnType.getKind() != TypeKind.DECLARED) {
            throw new Exception("Return type is invalid");
        }

        return returnType;
    }

}
