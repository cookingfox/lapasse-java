package com.cookingfox.lapasse.compiler.processor;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandResult;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventResult;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;
import java.util.LinkedList;
import java.util.List;

/**
 * Value Object for all processed annotated handler methods, for one origin (class).
 */
public class ProcessorResults {

    protected final List<HandleCommandResult> handleCommandResults = new LinkedList<>();
    protected final List<HandleEventResult> handleEventResults = new LinkedList<>();

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Adds a processed command handler result.
     *
     * @param result The processed command handler result to add.
     */
    public void addHandleCommandResult(HandleCommandResult result) {
        handleCommandResults.add(result);
    }

    /**
     * Adds a processed event handler result.
     *
     * @param result The processed event handler result to add.
     */
    public void addHandleEventResult(HandleEventResult result) {
        handleEventResults.add(result);
    }

    /**
     * Attempts to detect a conflict between the target {@link State} implementation (from
     * {@link #getTargetStateName()}) and the state type as defined by other command and event
     * handlers.
     *
     * @throws Exception when there is no target state name, or when there is a conflict,
     */
    public void detectTargetStateNameConflict() throws Exception {
        TypeName targetState = getTargetStateName();

        if (targetState == null) {
            throw new Exception("Can not determine target state");
        }

        for (HandleEventResult eventResult : handleEventResults) {
            TypeName eventState = ClassName.get(eventResult.getStateType());

            if (!eventState.equals(targetState)) {
                throw new Exception("Mapped event handler does not match expected concrete State");
            }
        }

        for (HandleCommandResult commandResult : handleCommandResults) {
            TypeMirror commandStateType = commandResult.getStateType();

            // it is allowed for handle command results to return a `null` state type
            if (commandStateType == null) {
                continue;
            }

            TypeName commandState = ClassName.get(commandStateType);

            if (!commandState.equals(targetState)) {
                throw new Exception("Mapped command handler does not match expected concrete State");
            }
        }
    }

    /**
     * @return Processor results for {@link HandleCommand} annotated methods.
     */
    public List<HandleCommandResult> getHandleCommandResults() {
        return handleCommandResults;
    }

    /**
     * @return Processor results for {@link HandleEvent} annotated methods.
     */
    public List<HandleEventResult> getHandleEventResults() {
        return handleEventResults;
    }

    /**
     * Attempts to determine the concrete {@link State} implementation for this origin (class).
     * Since event handler methods always return the State type, event handlers will be leading. If
     * no event handlers have been processed, the concrete state will be determined by the first
     * command handler that returns a valid state type.
     *
     * @return The concrete state implementation for this origin (class).
     */
    public TypeName getTargetStateName() {
        if (!handleEventResults.isEmpty()) {
            return ClassName.get(handleEventResults.get(0).getStateType());
        }

        for (HandleCommandResult result : handleCommandResults) {
            TypeMirror stateType = result.getStateType();

            if (stateType != null) {
                return ClassName.get(stateType);
            }
        }

        return null;
    }

}
