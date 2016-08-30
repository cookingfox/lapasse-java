package com.cookingfox.lapasse.samples.shared.tasks.command;

import com.cookingfox.lapasse.api.command.Command;

import java.util.Objects;

/**
 * Add task command.
 */
public final class AddTask implements Command {

    private final String text;

    public AddTask(String text) {
        this.text = Objects.requireNonNull(text);
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AddTask && o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return "AddTask{" +
                "text='" + text + '\'' +
                '}';
    }

}
