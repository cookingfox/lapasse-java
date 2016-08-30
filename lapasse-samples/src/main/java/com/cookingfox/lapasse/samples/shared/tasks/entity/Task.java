package com.cookingfox.lapasse.samples.shared.tasks.entity;

import java.util.Objects;
import java.util.UUID;

/**
 * Task entity.
 */
public final class Task {

    private final UUID id;
    private final String text;
    private final boolean completed;

    public Task(UUID id, String text, boolean completed) {
        this.id = Objects.requireNonNull(id);
        this.text = Objects.requireNonNull(text);
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Task && o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + (completed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", completed=" + completed +
                '}';
    }

}
