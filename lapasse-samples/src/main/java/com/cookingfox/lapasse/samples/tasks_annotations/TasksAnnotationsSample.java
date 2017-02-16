package com.cookingfox.lapasse.samples.tasks_annotations;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.impl.facade.LaPasseFacade;
import com.cookingfox.lapasse.samples.shared.tasks.command.AddTask;
import com.cookingfox.lapasse.samples.shared.tasks.command.CompleteTask;
import com.cookingfox.lapasse.samples.shared.tasks.command.RemoveTask;
import com.cookingfox.lapasse.samples.shared.tasks.event.TaskAdded;
import com.cookingfox.lapasse.samples.shared.tasks.state.TasksState;
import com.cookingfox.lapasse.samples.tasks_annotations.facade.TasksAnnotationsFacade;

import java.util.Collection;
import java.util.UUID;

/**
 * Sample application using core LaPasse library with annotations.
 */
public class TasksAnnotationsSample implements CombinedLogger<TasksState>, OnStateChanged<TasksState> {

    public static void main(String[] args) {
        new TasksAnnotationsSample().init();
    }

    private UUID firstCreatedTaskId;

    private void init() {
        System.out.println("SAMPLE: " + getClass().getSimpleName());

        // create initial state
        TasksState initialState = TasksState.createInitialState();

        // create facade
        TasksAnnotationsFacade facade = new TasksAnnotationsFacade(new LaPasseFacade.Builder<>(initialState).build());

        // add state changed listener
        facade.addStateChangedListener(this);

        // log operations
        facade.addLogger(this);

        System.out.println("\nINITIAL STATE: " + initialState);

        System.out.println("\n>>> ADD TASK");
        facade.handleCommand(new AddTask("First task"));

        System.out.println("\n>>> MARK TASK COMPLETE");
        facade.handleCommand(new CompleteTask(firstCreatedTaskId));

        System.out.println("\n>>> REMOVE TASK");
        facade.handleCommand(new RemoveTask(firstCreatedTaskId));
    }

    @Override
    public void onStateChanged(TasksState state, Event event) {
        System.out.println("STATE CHANGED: " + state);

        // task added: save generated task UUID so we can use it in subsequent commands
        if (event instanceof TaskAdded) {
            firstCreatedTaskId = ((TaskAdded) event).getTask().getId();
        }
    }

    @Override
    public void onCommandHandlerError(Throwable error, Command command) {
        error.printStackTrace();
    }

    @Override
    public void onCommandHandlerResult(Command command, Collection<Event> events) {
        System.out.println("COMMAND HANDLER RESULT: " + command);
    }

    @Override
    public void onEventHandlerError(Throwable error, Event event) {
        error.printStackTrace();
    }

    @Override
    public void onEventHandlerResult(Event event, TasksState newState) {
        System.out.println("EVENT HANDLER RESULT: " + event);
    }

}
