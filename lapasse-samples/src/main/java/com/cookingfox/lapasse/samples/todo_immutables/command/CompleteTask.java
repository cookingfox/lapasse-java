package com.cookingfox.lapasse.samples.todo_immutables.command;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.samples.todo_immutables.TodoStyle;
import org.immutables.value.Value;

import java.util.UUID;

/**
 * Complete task command.
 */
@TodoStyle
@Value.Immutable
public interface CompleteTask extends Command {

    UUID getTaskId();

    class Builder extends ImmutableCompleteTask.Builder {
    }

}
