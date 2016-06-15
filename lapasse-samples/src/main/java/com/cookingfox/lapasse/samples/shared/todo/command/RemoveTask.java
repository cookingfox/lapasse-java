package com.cookingfox.lapasse.samples.shared.todo.command;

import com.cookingfox.lapasse.api.command.Command;

import java.util.Objects;
import java.util.UUID;

/**
 * Remove task command.
 */
public final class RemoveTask implements Command {

    private final UUID taskId;

    public RemoveTask(UUID taskId) {
        this.taskId = Objects.requireNonNull(taskId);
    }

    public UUID getTaskId() {
        return taskId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RemoveTask && o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return taskId.hashCode();
    }

    @Override
    public String toString() {
        return "RemoveTask{" +
                "taskId=" + taskId +
                '}';
    }

}
