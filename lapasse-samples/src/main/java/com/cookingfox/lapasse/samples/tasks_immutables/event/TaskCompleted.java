package com.cookingfox.lapasse.samples.tasks_immutables.event;

import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.samples.tasks_immutables.TasksStyle;
import com.cookingfox.lapasse.samples.tasks_immutables.entity.Task;
import org.immutables.value.Value;

/**
 * Task completed event.
 */
@TasksStyle
@Value.Immutable
public interface TaskCompleted extends Event {

    Task getTask();

    class Builder extends ImmutableTaskCompleted.Builder {
    }

}
