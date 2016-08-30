package com.cookingfox.lapasse.samples.tasks_immutables.entity;

import com.cookingfox.lapasse.samples.tasks_immutables.TasksStyle;
import org.immutables.value.Value;

import java.util.UUID;

/**
 * Task entity.
 */
@TasksStyle
@Value.Immutable
public interface Task {

    UUID getId();

    String getText();

    boolean isCompleted();

    class Builder extends ImmutableTask.Builder {
    }

}
