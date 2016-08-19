package com.cookingfox.lapasse.compiler.processor;

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

    public void addHandleCommandResult(HandleCommandResult result) {
        handleCommandResults.add(result);
    }

    public void addHandleEventResult(HandleEventResult result) {
        handleEventResults.add(result);
    }

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

    public List<HandleCommandResult> getHandleCommandResults() {
        return handleCommandResults;
    }

    public List<HandleEventResult> getHandleEventResults() {
        return handleEventResults;
    }

    public TypeName getTargetStateName() {
        if (!handleEventResults.isEmpty()) {
            return ClassName.get(handleEventResults.get(0).getStateType());
        } else if (!handleCommandResults.isEmpty()) {
            for (HandleCommandResult result : handleCommandResults) {
                TypeMirror stateType = result.getStateType();

                if (stateType != null) {
                    return ClassName.get(stateType);
                }
            }
        }

        return null;
    }

}
