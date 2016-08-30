package com.cookingfox.lapasse.samples.tasks_immutables.command;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.samples.tasks_immutables.TasksStyle;
import org.immutables.value.Value;

/**
 * Add task command.
 */
@TasksStyle
@Value.Immutable
public interface AddTask extends Command {

    String getText();

    class Builder extends ImmutableAddTask.Builder {
    }

}
