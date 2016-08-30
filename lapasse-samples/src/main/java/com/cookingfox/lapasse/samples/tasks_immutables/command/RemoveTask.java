package com.cookingfox.lapasse.samples.tasks_immutables.command;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.samples.tasks_immutables.TasksStyle;
import org.immutables.value.Value;

import java.util.UUID;

/**
 * Remove task command.
 */
@TasksStyle
@Value.Immutable
public interface RemoveTask extends Command {

    UUID getTaskId();

    class Builder extends ImmutableRemoveTask.Builder {
    }

}
