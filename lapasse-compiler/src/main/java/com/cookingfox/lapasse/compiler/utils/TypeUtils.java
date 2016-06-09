package com.cookingfox.lapasse.compiler.utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Created by abeldebeer on 09/06/16.
 */
public final class TypeUtils {

    public static boolean firstArgIsSubType(TypeMirror typeMirror, Class otherType) {
        // no: undeclared type (e.g. void)
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }

        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        // no: invalid arguments size
        if (typeArguments.size() != 1) {
            return false;
        }

        // check sub type
        return isSubtype(typeArguments.get(0), otherType);
    }

    public static boolean isSubtype(TypeMirror typeMirror, Class otherType) {
        return isSubtype(typeMirror, otherType.getCanonicalName());
    }

    public static boolean isSubtype(TypeMirror typeMirror, String otherType) {
        // yes: same type
        if (otherType.equals(typeMirror.toString())) {
            return true;
        }

        // no: undeclared type (e.g. void)
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }

        DeclaredType declaredType = (DeclaredType) typeMirror;
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
