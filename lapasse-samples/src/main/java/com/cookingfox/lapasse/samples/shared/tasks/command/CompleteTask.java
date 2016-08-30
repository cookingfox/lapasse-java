package com.cookingfox.lapasse.samples.shared.tasks.command;

import com.cookingfox.lapasse.api.command.Command;

import java.util.Objects;
import java.util.UUID;

/**
 * Complete task command.
 */
public final class CompleteTask implements Command {

    private final UUID taskId;

    public CompleteTask(UUID taskId) {
        this.taskId = Objects.requireNonNull(taskId);
    }

    public UUID getTaskId() {
        return taskId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CompleteTask && o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return taskId.hashCode();
    }

    @Override
    public String toString() {
        return "CompleteTask{" +
                "taskId=" + taskId +
                '}';
    }

}
