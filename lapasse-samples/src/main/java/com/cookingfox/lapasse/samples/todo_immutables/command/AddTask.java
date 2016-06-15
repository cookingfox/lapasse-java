package com.cookingfox.lapasse.samples.todo_immutables.command;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.samples.todo_immutables.TodoStyle;
import org.immutables.value.Value;

/**
 * Add task command.
 */
@TodoStyle
@Value.Immutable
public interface AddTask extends Command {

    String getText();

    class Builder extends ImmutableAddTask.Builder {
    }

}
