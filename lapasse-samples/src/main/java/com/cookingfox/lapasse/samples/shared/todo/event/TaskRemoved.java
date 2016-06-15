package com.cookingfox.lapasse.samples.shared.todo.event;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.samples.shared.todo.entity.Task;

import java.util.Objects;

/**
 * Task removed event.
 */
public final class TaskRemoved implements Event {

    private final Task task;

    public TaskRemoved(Task task) {
        this.task = Objects.requireNonNull(task);
    }

    public Task getTask() {
        return task;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TaskRemoved && o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public String toString() {
        return "TaskRemoved{" +
                "task=" + task +
                '}';
    }

}
