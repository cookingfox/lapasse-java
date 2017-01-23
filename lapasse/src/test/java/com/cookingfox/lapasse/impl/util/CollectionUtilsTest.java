package com.cookingfox.lapasse.impl.util;

import org.junit.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertNotNull;
import static testing.TestingUtils.assertPrivateConstructorInstantiationUnsupported;

/**
 * Unit tests for {@link CollectionUtils}.
 */
public class CollectionUtilsTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_should_throw() throws Exception {
        assertPrivateConstructorInstantiationUnsupported(CollectionUtils.class);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: newConcurrentMap
    //----------------------------------------------------------------------------------------------

    @Test
    public void newConcurrentMap_should_create_new_map() throws Exception {
        ConcurrentMap<Integer, String> result = CollectionUtils.newConcurrentMap();

        assertNotNull(result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: newConcurrentSet
    //----------------------------------------------------------------------------------------------

    @Test
    public void newConcurrentSet_should_create_new_set() throws Exception {
        Set<String> result = CollectionUtils.newConcurrentSet();

        assertNotNull(result);
    }

}
