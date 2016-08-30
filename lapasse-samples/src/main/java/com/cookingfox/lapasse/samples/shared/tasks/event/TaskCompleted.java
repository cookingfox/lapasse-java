package com.cookingfox.lapasse.samples.shared.tasks.event;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.samples.shared.tasks.entity.Task;

import java.util.Objects;

/**
 * Task completed event.
 */
public final class TaskCompleted implements Event {

    private final Task task;

    public TaskCompleted(Task task) {
        this.task = Objects.requireNonNull(task);
    }

    public Task getTask() {
        return task;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TaskCompleted && o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public String toString() {
        return "TaskCompleted{" +
                "task=" + task +
                '}';
    }

}
