package com.cookingfox.lapasse.samples.tasks_immutables;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.logging.CombinedLogger;
import com.cookingfox.lapasse.api.state.observer.OnStateChanged;
import com.cookingfox.lapasse.impl.facade.LaPasseFacade;
import com.cookingfox.lapasse.samples.tasks_immutables.command.AddTask;
import com.cookingfox.lapasse.samples.tasks_immutables.command.CompleteTask;
import com.cookingfox.lapasse.samples.tasks_immutables.command.RemoveTask;
import com.cookingfox.lapasse.samples.tasks_immutables.event.TaskAdded;
import com.cookingfox.lapasse.samples.tasks_immutables.facade.TasksFacadeImmutables;
import com.cookingfox.lapasse.samples.tasks_immutables.state.TasksState;

import java.util.Collection;
import java.util.UUID;

/**
 * Sample application using core LaPasse library with annotations.
 */
public class TasksSampleImmutables implements CombinedLogger<TasksState>, OnStateChanged<TasksState> {

    public static void main(String[] args) {
        new TasksSampleImmutables().init();
    }

    private UUID firstCreatedTaskId;

    private void init() {
        System.out.println("SAMPLE: " + getClass().getSimpleName());

        // create initial state
        TasksState initialState = TasksState.createInitialState();

        // create facade
        TasksFacadeImmutables facade = new TasksFacadeImmutables(new LaPasseFacade.Builder<>(initialState).build());

        // add state changed listener
        facade.addStateChangedListener(this);

        // log operations
        facade.addLogger(this);

        System.out.println("\nINITIAL STATE: " + initialState);

        System.out.println("\n>>> ADD TASK");
        facade.handleCommand(new AddTask.Builder().text("First task").build());

        System.out.println("\n>>> MARK TASK COMPLETE");
        facade.handleCommand(new CompleteTask.Builder().taskId(firstCreatedTaskId).build());

        System.out.println("\n>>> REMOVE TASK");
        facade.handleCommand(new RemoveTask.Builder().taskId(firstCreatedTaskId).build());
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
    public void onCommandHandlerError(Throwable error, Command command, Collection<Event> events) {
        error.printStackTrace();
    }

    @Override
    public void onCommandHandlerResult(Command command, Collection<Event> events) {
        System.out.println("COMMAND HANDLER RESULT: " + command);
    }

    @Override
    public void onEventHandlerError(Throwable error, Event event, TasksState newState) {
        error.printStackTrace();
    }

    @Override
    public void onEventHandlerResult(Event event, TasksState newState) {
        System.out.println("EVENT HANDLER RESULT: " + event);
    }

}
