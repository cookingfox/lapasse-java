package com.cookingfox.lapasse.compiler;

/**
 * Created by abeldebeer on 09/06/16.
 */
public interface Validator {
    String getError();

    boolean isValid();
}
