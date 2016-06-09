package com.cookingfox.lapasse.compiler.command;

import com.cookingfox.lapasse.api.event.Event;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import static com.cookingfox.lapasse.compiler.utils.TypeUtils.firstArgIsSubType;
import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;

/**
 * Created by abeldebeer on 09/06/16.
 */
public class HandleCommandReturns extends AbstractHandleCommand {

    protected TypeMirror eventType;
    protected ExecutableElement executableElement;
    protected DeclaredType returnType;

    protected boolean isDeclaredType = false;
    protected boolean returnsCallable = false;
    protected boolean returnsCollection = false;
    protected boolean returnsEvent = false;
    protected boolean returnsEventCallable = false;
    protected boolean returnsEventCollection = false;
    protected boolean returnsEventCollectionCallable = false;
    protected boolean returnsVoid = false;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleCommandReturns(Element element) {
        super(element);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public String getError() {
        String prefix = String.format("Return type of @%s annotated method", ANNOTATION);

        if (isValid()) {
            return String.format("%s is valid", prefix);
        }

        if (!isDeclaredType) {
            return String.format("%s is not declared", prefix);
        }

        return String.format("%1$s is expected to be one of the following: `void`, `%2$s`, " +
                        "`%3$s<%2$s>`, `%4$s<%2$s>`, `%4$s<%3$s<%2$s>>`", prefix,
                Event.class.getSimpleName(),
                Collection.class.getSimpleName(),
                Callable.class.getSimpleName());
    }

    public TypeName getEventName() {
        return TypeName.get(eventType);
    }

    public TypeName getMethodReturnTypeName() {
        if (returnsVoid) {
            return TypeName.VOID;
        }

        return TypeName.get(returnType);
    }

    @Override
    public boolean isValid() {
        if (returnsVoid) {
            // void is valid return type for a command handler
            return true;
        } else if (!isDeclaredType) {
            return false;
        }

        return returnsEvent ||
                returnsEventCallable ||
                returnsEventCollection ||
                returnsEventCollectionCallable;
    }

    public boolean returnsEventCallable() {
        return returnsEventCallable;
    }

    public boolean returnsEventCollection() {
        return returnsEventCollection;
    }

    public boolean returnsEventCollectionCallable() {
        return returnsEventCollectionCallable;
    }

    public boolean returnsVoid() {
        return returnsVoid;
    }

    public void setExecutableElement(ExecutableElement executableElement) {
        this.executableElement = executableElement;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void doProcess() {
        if (returnsVoid = validateReturnsVoid()) {
            return;
        }

        if (isDeclaredType = validateDeclaredType()) {
            validateDeclaredReturnType();
        }
    }

    protected boolean validateDeclaredType() {
        TypeMirror mirrorReturnType = executableElement.getReturnType();

        if (mirrorReturnType.getKind() != TypeKind.DECLARED) {
            return false;
        }

        returnType = (DeclaredType) mirrorReturnType;

        return true;
    }

    private void validateDeclaredReturnType() {
        if (isSubtype(returnType, Event.class)) {
            returnsEvent = true;
            setEventTypeWith(returnType);

            // event is valid return type: no further inspection necessary
            return;
        } else if (isSubtype(returnType, Collection.class)) {
            // is collection, need to inspect generic
            returnsCollection = true;
        } else if (isSubtype(returnType, Callable.class)) {
            // is callable, need to inspect generic
            returnsCallable = true;
        } else {
            // no valid return type
            return;
        }

        List<? extends TypeMirror> typeArguments = returnType.getTypeArguments();

        // check whether the generic type of the first argument is `Event`
        if (firstArgIsSubType(returnType, Event.class)) {
            returnsEventCallable = returnsCallable;
            returnsEventCollection = returnsCollection;

            setEventTypeWith(typeArguments.get(0));
        } else if (returnsCallable && typeArguments.size() == 1) {
            DeclaredType firstArg = (DeclaredType) typeArguments.get(0);

            // check whether the generic type of the callable is `Collection<Event>`
            if (firstArgIsSubType(firstArg, Event.class)) {
                returnsEventCollectionCallable = true;

                setEventTypeWith(firstArg.getTypeArguments().get(0));
            }
        }
    }

    private void setEventTypeWith(TypeMirror type) {
        eventType = type;
    }

    private boolean validateReturnsVoid() {
        return executableElement.getReturnType().getKind() == TypeKind.VOID;
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HandleCommandReturns{" +
                "isDeclaredType=" + isDeclaredType +
                ", returnsVoid=" + returnsVoid +
                ", returnsEvent=" + returnsEvent +
                ", returnsEventCallable=" + returnsEventCallable +
                ", returnsEventCollection=" + returnsEventCollection +
                ", returnsEventCollectionCallable=" + returnsEventCollectionCallable +
                '}';
    }

}
