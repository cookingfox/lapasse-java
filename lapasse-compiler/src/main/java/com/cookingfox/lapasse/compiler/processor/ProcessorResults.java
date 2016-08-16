package com.cookingfox.lapasse.compiler.processor;

import com.cookingfox.lapasse.compiler.processor.command.HandleCommandResult;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventResult;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by abeldebeer on 15/08/16.
 */
public class ProcessorResults {

    protected final List<HandleCommandResult> handleCommandResults = new LinkedList<>();
    protected final List<HandleEventResult> handleEventResults = new LinkedList<>();

    public void addHandleCommandResult(HandleCommandResult result) {
        handleCommandResults.add(result);
    }

    public void addHandleEventResult(HandleEventResult result) {
        handleEventResults.add(result);
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

    @Override
    public String toString() {
        return "ProcessorResults{" +
                "handleCommandResults=" + handleCommandResults +
                ", handleEventResults=" + handleEventResults +
                '}';
    }

}
