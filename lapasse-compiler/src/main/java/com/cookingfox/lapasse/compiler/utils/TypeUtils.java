package com.cookingfox.lapasse.compiler.utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Utilities for working with types and elements from the {@link javax.lang.model} package.
 */
public final class TypeUtils {

    /**
     * Constructor disabled.
     */
    private TypeUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean equalsType(TypeMirror typeMirror, Class<?> aClass) {
        // clean class name and compare to type mirror
        return aClass.getName().replace('$', '.').equals(typeMirror.toString());
    }

    /**
     * Performs operations to determine whether the first argument (generic parameter) of `type` is
     * a subtype of `otherType`.
     *
     * @param type      The parameterized type to validate.
     * @param otherType The subtype.
     * @return Whether the first argument (generic parameter) of `type` is a subtype of `otherType`.
     */
    public static boolean firstArgIsSubType(TypeMirror type, Class otherType) {
        // no: undeclared type (e.g. void)
        if (type.getKind() != TypeKind.DECLARED) {
            return false;
        }

        DeclaredType declaredType = (DeclaredType) type;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        // no: invalid arguments size
        if (typeArguments.size() != 1) {
            return false;
        }

        // check sub type
        return isSubtype(typeArguments.get(0), otherType);
    }

    public static boolean isSubtype(Element element, Class<?> aClass) {
        return isSubtype(element.asType(), aClass);
    }

    /**
     * Performs operations to determine whether `type` is a subtype of `otherType`.
     *
     * @param type      The type to validate.
     * @param otherType The subtype.
     * @return Whether `type` is a subtype of `otherType`.
     */
    public static boolean isSubtype(TypeMirror type, Class otherType) {
        return isSubtype(type, otherType.getCanonicalName());
    }

    /**
     * Performs operations to determine whether `type` is a subtype of `otherType`.
     *
     * @param type      The type to validate.
     * @param otherType The subtype.
     * @return Whether `type` is a subtype of `otherType`.
     */
    public static boolean isSubtype(TypeMirror type, String otherType) {
        // yes: same type
        if (otherType.equals(type.toString())) {
            return true;
        }

        // no: undeclared type (e.g. void)
        if (type.getKind() != TypeKind.DECLARED) {
            return false;
        }

        DeclaredType declaredType = (DeclaredType) type;
        String typeString = declaredType.toString();
        int genericStartPos = typeString.indexOf('<');

        // yes: matches type stripped of generics
        if (genericStartPos > 0 && typeString.substring(0, genericStartPos).equals(otherType)) {
            return true;
        }

        Element element = declaredType.asElement();

        // no: not a type element
        if (!(element instanceof TypeElement)) {
            return false;
        }

        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();

        // yes: extends class
        if (isSubtype(superType, otherType)) {
            return true;
        }

        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            // yes: implements interface
            if (isSubtype(interfaceType, otherType)) {
                return true;
            }
        }

        // no match found
        return false;
    }

}
