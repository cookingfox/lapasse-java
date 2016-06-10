package com.cookingfox.lapasse.compiler.base;

/**
 * Interface for adding simple validation to a class.
 */
public interface Validator {

    /**
     * Returns the error message if {@link #isValid()} returns false.
     *
     * @return Error message.
     */
    String getError();

    /**
     * Returns whether validation has succeeded.
     *
     * @return Whether validation has succeeded.
     */
    boolean isValid();

}
