package com.cookingfox.lapasse.compiler.processor;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Static helper methods for an annotation processor.
 */
public final class ProcessorHelper {

    /**
     * List of modifiers that are not allowed to be present on an annotated method.
     */
    protected static final List<Modifier> FORBIDDEN_MODIFIERS;

    static {
        FORBIDDEN_MODIFIERS = Collections.unmodifiableList(Arrays.asList(
                Modifier.ABSTRACT,
                Modifier.NATIVE,
                Modifier.PRIVATE,
                Modifier.STATIC,
                Modifier.STRICTFP,
                Modifier.VOLATILE
        ));
    }

    /**
     * Asserts that the provided element is a valid method and returns it as an executable element.
     *
     * @param element The element to validate.
     * @return The executable element.
     * @throws Exception when the element is not a valid method.
     */
    public static ExecutableElement validateAndGetAnnotatedMethod(Element element) throws Exception {
        // annotated method is not accessible
        if (!Collections.disjoint(element.getModifiers(), FORBIDDEN_MODIFIERS)) {
            throw new Exception("Method is not accessible - it must be a non-static method with " +
                    "public, protected or package-level access");
        }

        ExecutableElement method = (ExecutableElement) element;

        // check if method has a "throws" statement
        if (!method.getThrownTypes().isEmpty()) {
            throw new Exception("Handler methods are not allowed to declare a `throws` clause. " +
                    "If possible, create an event for the 'exceptional' use case. If you really " +
                    "need to throw, use an unchecked exception (extends `RuntimeException`).");
        }

        boolean hasCommandAnnotation = method.getAnnotation(HandleCommand.class) != null;
        boolean hasEventAnnotation = method.getAnnotation(HandleEvent.class) != null;

        // cannot have both annotations
        if (hasCommandAnnotation && hasEventAnnotation) {
            throw new Exception("Annotated handler method can not have both a command and an " +
                    "event annotation");
        }

        return method;
    }

    /**
     * Constructor disabled.
     */
    private ProcessorHelper() {
        throw new UnsupportedOperationException();
    }

}
