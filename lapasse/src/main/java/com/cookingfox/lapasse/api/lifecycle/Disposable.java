package com.cookingfox.lapasse.api.lifecycle;

/**
 * Provides the ability to dispose a component.
 */
public interface Disposable {

    /**
     * Hook for cleaning up: remove listeners, shutdown threads, etc.
     */
    void dispose();

}
