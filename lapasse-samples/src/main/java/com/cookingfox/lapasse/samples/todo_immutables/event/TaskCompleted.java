package com.cookingfox.lapasse.samples.todo_immutables.event;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.samples.todo_immutables.TodoStyle;
import com.cookingfox.lapasse.samples.todo_immutables.entity.Task;
import org.immutables.value.Value;

/**
 * Task completed event.
 */
@TodoStyle
@Value.Immutable
public interface TaskCompleted extends Event {

    Task getTask();

    class Builder extends ImmutableTaskCompleted.Builder {
    }

}
