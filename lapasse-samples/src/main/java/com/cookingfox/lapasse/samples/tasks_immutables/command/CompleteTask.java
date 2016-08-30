package com.cookingfox.lapasse.samples.tasks_immutables.command;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.samples.tasks_immutables.TasksStyle;
import org.immutables.value.Value;

import java.util.UUID;

/**
 * Complete task command.
 */
@TasksStyle
@Value.Immutable
public interface CompleteTask extends Command {

    UUID getTaskId();

    class Builder extends ImmutableCompleteTask.Builder {
    }

}
