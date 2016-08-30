package com.cookingfox.lapasse.samples.shared.tasks.state;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.samples.shared.tasks.entity.Task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import static java.util.Collections.unmodifiableList;

/**
 * State object for a list of tasks that can be completed.
 */
public final class TasksState implements State {

    private final Collection<Task> tasks;

    public TasksState(Collection<Task> tasks) {
        this.tasks = unmodifiableList(new LinkedList<>(tasks));
    }

    public Task findTaskById(UUID taskId) {
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }

        return null;
    }

    public Collection<Task> getTasks() {
        return tasks;
    }

    public static TasksState createInitialState() {
        return new TasksState(new LinkedList<Task>());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TasksState && o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return tasks.hashCode();
    }

    @Override
    public String toString() {
        return "TasksState{" +
                "tasks=" + tasks +
                '}';
    }

}
