package com.cookingfox.lapasse.impl.helper.exception;

import com.cookingfox.lapasse.api.exception.LaPasseException;
import com.cookingfox.lapasse.impl.internal.HandlerMapper;

import java.lang.reflect.Constructor;

/**
 * Thrown when an error occurs during the instantiation of a generated {@link HandlerMapper} class.
 */
public class HandlerMapperInstantiationException extends LaPasseException {

    public HandlerMapperInstantiationException(Constructor<? extends HandlerMapper> constructor, Throwable cause) {
        super("Could not create an instance of handler mapper: " + constructor.toString(), cause);
    }

}
