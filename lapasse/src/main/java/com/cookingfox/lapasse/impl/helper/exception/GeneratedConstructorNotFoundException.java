package com.cookingfox.lapasse.impl.helper.exception;

import com.cookingfox.lapasse.api.exception.LaPasseException;

/**
 * Thrown when the expected generated constructor is not found.
 */
public class GeneratedConstructorNotFoundException extends LaPasseException {

    public GeneratedConstructorNotFoundException(String fqcn, Throwable cause) {
        super("Expected generated constructor not found for: " + fqcn, cause);
    }

}
