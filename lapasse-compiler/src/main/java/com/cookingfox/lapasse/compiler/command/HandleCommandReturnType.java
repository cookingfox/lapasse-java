package com.cookingfox.lapasse.compiler.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.api.event.Event;
import com.squareup.javapoet.TypeName;
import rx.Observable;

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
 * Processes the return type of a {@link HandleCommand} annotated method.
 */
public class HandleCommandReturnType extends AbstractHandleCommand {

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
    protected boolean returnsEventCollectionObservable = false;
    protected boolean returnsEventObservable = false;
    protected boolean returnsObservable = false;
    protected boolean returnsVoid = false;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public HandleCommandReturnType(Element element) {
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

        return String.format("%1$s is expected to be one of the following: " +
                        "`void`, " + // void
                        "`%2$s`, " + // Event
                        "`%3$s<%2$s>`, " + // Collection<Event>
                        "`%4$s<%2$s>`, " + // Callable<Event>
                        "`%4$s<%3$s<%2$s>>`, " + // Callable<Collection<Event>>
                        "`%5$s<%2$s>`, " + // Observable<Event>
                        "`%5$s<%3$s<%2$s>>`", // Observable<Collection<Event>>
                prefix,
                Event.class.getSimpleName(),
                Collection.class.getSimpleName(),
                Callable.class.getSimpleName(),
                Observable.class.getName());
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
                returnsEventCollection ||
                returnsEventCallable ||
                returnsEventObservable ||
                returnsEventCollectionCallable ||
                returnsEventCollectionObservable;
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

    public boolean returnsEventCollectionObservable() {
        return returnsEventCollectionObservable;
    }

    public boolean returnsEventObservable() {
        return returnsEventObservable;
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

    protected void validateDeclaredReturnType() {
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
        } else if (isSubtype(returnType, Observable.class)) {
            // is observable, need to inspect generic
            returnsObservable = true;
        } else {
            // no valid return type
            return;
        }

        List<? extends TypeMirror> typeArguments = returnType.getTypeArguments();

        // check whether the generic type of the first argument is `Event`
        if (firstArgIsSubType(returnType, Event.class)) {
            returnsEventCallable = returnsCallable;
            returnsEventCollection = returnsCollection;
            returnsEventObservable = returnsObservable;

            setEventTypeWith(typeArguments.get(0));
        } else if (typeArguments.size() == 1 && (returnsCallable || returnsObservable)) {
            DeclaredType firstArg = (DeclaredType) typeArguments.get(0);

            // check whether the generic type of the callable is `Collection<Event>`
            if (firstArgIsSubType(firstArg, Event.class)) {
                returnsEventCollectionCallable = returnsCallable;
                returnsEventCollectionObservable = returnsObservable;

                setEventTypeWith(firstArg.getTypeArguments().get(0));
            }
        }
    }

    protected void setEventTypeWith(TypeMirror type) {
        eventType = type;
    }

    protected boolean validateReturnsVoid() {
        return executableElement.getReturnType().getKind() == TypeKind.VOID;
    }

    //----------------------------------------------------------------------------------------------
    // OBJECT OVERRIDES
    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "HandleCommandReturnType{" +
                "isDeclaredType=" + isDeclaredType +
                ", returnType=" + returnType +
                ", returnsVoid=" + returnsVoid +
                ", returnsEvent=" + returnsEvent +
                ", returnsEventCollection=" + returnsEventCollection +
                ", returnsEventCallable=" + returnsEventCallable +
                ", returnsEventObservable=" + returnsEventObservable +
                ", returnsEventCollectionCallable=" + returnsEventCollectionCallable +
                ", returnsEventCollectionObservable=" + returnsEventCollectionObservable +
                '}';
    }

}
