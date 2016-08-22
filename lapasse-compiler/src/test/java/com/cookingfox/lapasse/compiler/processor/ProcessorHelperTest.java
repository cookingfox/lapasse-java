package com.cookingfox.lapasse.compiler.processor;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for {@link ProcessorHelper}.
 */
public class ProcessorHelperTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = InvocationTargetException.class)
    public void constructor_should_throw() throws Exception {
        Constructor<ProcessorHelper> constructor = ProcessorHelper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
