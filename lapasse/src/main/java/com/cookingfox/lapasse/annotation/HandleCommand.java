package com.cookingfox.lapasse.annotation;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.handler.CommandHandler;
import com.cookingfox.lapasse.api.state.State;

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

    /**
     * Optionally specify the concrete Command class that will be handled. This is only necessary
     * when the `command` parameter is omitted from the annotated handler method.
     *
     * @return The concrete Command class.
     */
    Class<? extends Command> command() default Command.class;

    /**
     * Optionally specify the concrete State class that will be handled. This is only necessary
     * when the `state` parameter is omitted from the annotated handler method, and it is not
     * possible to derive the concrete State from the event or other handler methods.
     *
     * @return The concrete State class.
     */
    Class<? extends State> state() default State.class;

}
