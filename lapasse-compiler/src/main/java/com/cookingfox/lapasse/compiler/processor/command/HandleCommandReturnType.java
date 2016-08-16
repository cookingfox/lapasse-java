package com.cookingfox.lapasse.compiler.processor.command;

import com.cookingfox.lapasse.api.event.Event;
import rx.Observable;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by abeldebeer on 16/08/16.
 */
public enum HandleCommandReturnType {

    /**
     * The method returns an {@link Event}.
     */
    RETURNS_EVENT,

    /**
     * The method returns a {@link Callable} of an {@link Event}.
     */
    RETURNS_EVENT_CALLABLE,

    /**
     * The method returns a {@link Collection} of {@link Event}s.
     */
    RETURNS_EVENT_COLLECTION,

    /**
     * The method returns a {@link Callable} of a {@link Collection} of {@link Event}s.
     */
    RETURNS_EVENT_COLLECTION_CALLABLE,

    /**
     * The method returns an {@link Observable} of a {@link Collection} of {@link Event}s.
     */
    RETURNS_EVENT_COLLECTION_OBSERVABLE,

    /**
     * The method returns an {@link Observable} of an {@link Event}.
     */
    RETURNS_EVENT_OBSERVABLE,

    /**
     * The method returns void.
     */
    RETURNS_VOID

}
