package com.cookingfox.lapasse.samples.tasks_immutables.state;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.samples.tasks_immutables.entity.Task;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * State object for a list of tasks that can be completed.
 */
// @TasksStyle // don't apply style for abstract class: doesn't have desired effect
@Value.Immutable
public abstract class TasksState implements State {

    public abstract Collection<Task> getTasks();

    public Task findTaskById(UUID taskId) {
        for (Task task : getTasks()) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }

        return null;
    }

    public static TasksState createInitialState() {
        return builder()
                .tasks(new ArrayList<Task>())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public final static class Builder extends ImmutableTasksState.Builder {
    }

}
