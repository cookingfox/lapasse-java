package com.cookingfox.lapasse.annotation;

import com.cookingfox.lapasse.api.event.handler.EventHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method with this annotation should be interpreted as a short-hand
 * {@link EventHandler} implementation.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface HandleEvent {
}
