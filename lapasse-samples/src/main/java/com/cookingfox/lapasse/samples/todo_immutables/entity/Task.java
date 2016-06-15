package com.cookingfox.lapasse.samples.todo_immutables.entity;

import com.cookingfox.lapasse.samples.todo_immutables.TodoStyle;
import org.immutables.value.Value;

import java.util.UUID;

/**
 * Task entity.
 */
@TodoStyle
@Value.Immutable
public interface Task {

    UUID getId();

    String getText();

    boolean isCompleted();

    class Builder extends ImmutableTask.Builder {
    }

}
