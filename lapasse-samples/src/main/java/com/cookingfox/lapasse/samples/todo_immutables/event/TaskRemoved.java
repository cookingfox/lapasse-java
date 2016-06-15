package com.cookingfox.lapasse.samples.todo_immutables.event;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.samples.todo_immutables.TodoStyle;
import com.cookingfox.lapasse.samples.todo_immutables.entity.Task;
import org.immutables.value.Value;

/**
 * Task removed event.
 */
@TodoStyle
@Value.Immutable
public interface TaskRemoved extends Event {

    Task getTask();

    class Builder extends ImmutableTaskRemoved.Builder {
    }

}
