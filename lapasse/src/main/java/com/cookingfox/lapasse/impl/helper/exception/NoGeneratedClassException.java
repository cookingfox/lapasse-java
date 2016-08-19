package com.cookingfox.lapasse.impl.helper.exception;

import com.cookingfox.lapasse.api.exception.LaPasseException;

/**
 * Thrown when a generated class does not exist.
 */
public class NoGeneratedClassException extends LaPasseException {

    public NoGeneratedClassException(String generatedClassName) {
        super("Generated class does not exist. Does the class contain LaPasse annotations? " +
                "Expected: " + generatedClassName);
    }

}
