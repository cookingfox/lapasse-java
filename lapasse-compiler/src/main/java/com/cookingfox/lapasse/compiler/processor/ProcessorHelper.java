package com.cookingfox.lapasse.compiler.processor;

import javax.lang.model.element.Element;
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
     * Returns whether the provided element has the expected access modifiers.
     *
     * @param element The element to check.
     * @return Whether the element is accessible.
     */
    public static boolean isAccessible(Element element) {
        return Collections.disjoint(element.getModifiers(), FORBIDDEN_MODIFIERS);
    }

    /**
     * Constructor disabled.
     */
    private ProcessorHelper() {
        throw new UnsupportedOperationException();
    }

}
