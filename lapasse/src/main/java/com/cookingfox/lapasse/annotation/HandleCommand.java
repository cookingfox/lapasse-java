package com.cookingfox.lapasse.annotation;

import com.cookingfox.lapasse.api.command.handler.CommandHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method with this annotation should be interpreted as a short-hand
 * {@link CommandHandler} implementation.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface HandleCommand {
}
