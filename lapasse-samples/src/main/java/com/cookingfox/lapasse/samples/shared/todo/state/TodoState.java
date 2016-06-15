package com.cookingfox.lapasse.samples.shared.todo.state;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.samples.shared.todo.entity.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * State object for a list of tasks that can be completed.
 */
public final class TodoState implements State {

    private final Collection<Task> tasks;

    public TodoState(Collection<Task> tasks) {
        this.tasks = unmodifiableList(new ArrayList<>(requireNonNull(tasks)));
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

    @Override
    public boolean equals(Object o) {
        return o instanceof TodoState && o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return tasks.hashCode();
    }

    @Override
    public String toString() {
        return "TodoState{" +
                "tasks=" + tasks +
                '}';
    }

}
