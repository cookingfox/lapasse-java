package com.cookingfox.lapasse.compiler.processor;

import org.junit.Test;

import static testing.TestingUtils.assertPrivateConstructorInstantiationUnsupported;

/**
 * Unit tests for {@link ProcessorHelper}.
 */
public class ProcessorHelperTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_should_throw() throws Exception {
        assertPrivateConstructorInstantiationUnsupported(ProcessorHelper.class);
    }

}
