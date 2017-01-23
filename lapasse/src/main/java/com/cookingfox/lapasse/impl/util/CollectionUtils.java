package com.cookingfox.lapasse.impl.util;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility methods for working with collections.
 */
public final class CollectionUtils {

    //----------------------------------------------------------------------------------------------
    // PRIVATE CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    private CollectionUtils() {
        throw new UnsupportedOperationException();
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new concurrent map using the default implementation.
     *
     * @param <K> The type of keys maintained by this map.
     * @param <V> The type of mapped values.
     * @return New concurrent map.
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Creates a new concurrent set from a {@link ConcurrentMap}.
     *
     * @param <V> The value type.
     * @return New concurrent set.
     */
    public static <V> Set<V> newConcurrentSet() {
        return Collections.newSetFromMap(CollectionUtils.<V, Boolean>newConcurrentMap());
    }

}
