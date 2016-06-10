package com.cookingfox.lapasse.impl.helper;

import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.impl.helper.exception.GeneratedConstructorNotFoundException;
import com.cookingfox.lapasse.impl.helper.exception.HandlerMapperInstantiationException;
import com.cookingfox.lapasse.impl.helper.exception.NoGeneratedClassException;
import com.cookingfox.lapasse.impl.internal.HandlerMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Helper class for mapping handlers from annotated class.
 */
public final class LaPasseHelper {

    /**
     * Generated class name suffix.
     */
    public static final String GENERATED_SUFFIX = "$$LaPasseGenerated";

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * Not meant to be instantiated.
     */
    private LaPasseHelper() {
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Map the annotated handler methods of a class to its facade.
     *
     * @param origin The class containing the annotations.
     * @param facade The LaPasse facade to map handlers to.
     * @param <T>    Indicates the origin type.
     * @throws NoGeneratedClassException             when no generated class exists for this origin.
     * @throws GeneratedConstructorNotFoundException when the expected generated constructor was not
     *                                               found.
     * @throws HandlerMapperInstantiationException   when an error occurs during the instantiation
     *                                               of the HandlerMapper.
     */
    public static <T> void mapHandlers(T origin, Facade<? extends State> facade) {
        Objects.requireNonNull(origin, "Origin can not be null");
        Objects.requireNonNull(facade, "Facade can not be null");

        // get generated class name
        Class<?> originClass = origin.getClass();
        String generatedClassName = originClass.getCanonicalName() + GENERATED_SUFFIX;

        // create instance of generated class
        Class<? extends HandlerMapper> handlerMapperClass = getHandlerMapperClass(generatedClassName);
        Constructor<? extends HandlerMapper> constructor = getHandlerMapperConstructor(handlerMapperClass, originClass);
        HandlerMapper handlerMapper = createHandlerMapperInstance(constructor, origin, facade);

        // map handlers
        handlerMapper.mapHandlers();
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates an instance of the provided HandlerMapper constructor.
     *
     * @param constructor The constructor to create an instance of.
     * @param origin      The annotated class to pass to the constructor.
     * @param facade      The facade to pass to the constructor.
     * @return A HandlerMapper instance.
     * @throws HandlerMapperInstantiationException when an error occurs during the instantiation of
     *                                             the HandlerMapper.
     */
    protected static HandlerMapper createHandlerMapperInstance(
            Constructor<? extends HandlerMapper> constructor, Object origin, Facade facade) {
        try {
            return constructor.newInstance(origin, facade);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new HandlerMapperInstantiationException(constructor, e);
        }
    }

    /**
     * Attempts to load the HandlerMapper class by its Fully-Qualified Class Name.
     *
     * @param fqcn Fully-Qualified Class Name.
     * @return The handler mapper class.
     * @throws NoGeneratedClassException when no generated class exists for this FQCN.
     */
    protected static Class<? extends HandlerMapper> getHandlerMapperClass(String fqcn) {
        try {
            // noinspection unchecked
            return (Class<? extends HandlerMapper>) Class.forName(fqcn);
        } catch (ClassNotFoundException e) {
            throw new NoGeneratedClassException(fqcn);
        }
    }

    /**
     * Attempts to get the default generated HandlerMapper constructor.
     *
     * @param handlerMapperClass HandlerMapper class.
     * @param originClass        The class containing the annotations.
     * @return The generated HandlerMapper constructor.
     * @throws GeneratedConstructorNotFoundException when the expected generated constructor was not
     *                                               found.
     */
    protected static Constructor<? extends HandlerMapper> getHandlerMapperConstructor(
            Class<? extends HandlerMapper> handlerMapperClass, Class<?> originClass) {
        try {
            return handlerMapperClass.getDeclaredConstructor(originClass, Facade.class);
        } catch (NoSuchMethodException e) {
            throw new GeneratedConstructorNotFoundException(handlerMapperClass.getCanonicalName(), e);
        }
    }

}
