package com.cookingfox.lapasse.samples.tasks_vanilla.facade;

import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.facade.LaPasseFacadeDelegate;
import com.cookingfox.lapasse.samples.shared.tasks.command.AddTask;
import com.cookingfox.lapasse.samples.shared.tasks.command.CompleteTask;
import com.cookingfox.lapasse.samples.shared.tasks.command.RemoveTask;
import com.cookingfox.lapasse.samples.shared.tasks.entity.Task;
import com.cookingfox.lapasse.samples.shared.tasks.event.TaskAdded;
import com.cookingfox.lapasse.samples.shared.tasks.event.TaskCompleted;
import com.cookingfox.lapasse.samples.shared.tasks.event.TaskRemoved;
import com.cookingfox.lapasse.samples.shared.tasks.exception.TasksException;
import com.cookingfox.lapasse.samples.shared.tasks.state.TasksState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * LaPasse {@link Facade} delegate containing the command and event handlers.
 */
public final class TasksFacadeVanilla extends LaPasseFacadeDelegate<TasksState> {

    public TasksFacadeVanilla(Facade<TasksState> facade) {
        super(facade);

        facade.mapCommandHandler(AddTask.class, handleAddTask);
        facade.mapCommandHandler(CompleteTask.class, handleCompleteTask);
        facade.mapCommandHandler(RemoveTask.class, handleRemoveTask);

        facade.mapEventHandler(TaskAdded.class, handleTaskAdded);
        facade.mapEventHandler(TaskCompleted.class, handleTaskCompleted);
        facade.mapEventHandler(TaskRemoved.class, handleTaskRemoved);
    }

    //----------------------------------------------------------------------------------------------
    // ADD TASK
    //----------------------------------------------------------------------------------------------

    final SyncCommandHandler<TasksState, AddTask, TaskAdded> handleAddTask =
            new SyncCommandHandler<TasksState, AddTask, TaskAdded>() {
                @Override
                public TaskAdded handle(TasksState state, AddTask command) {
                    Task task = new Task(UUID.randomUUID(), command.getText(), false);

                    return new TaskAdded(task);
                }
            };

    final EventHandler<TasksState, TaskAdded> handleTaskAdded =
            new EventHandler<TasksState, TaskAdded>() {
                @Override
                public TasksState handle(TasksState state, TaskAdded event) {
                    Collection<Task> tasks = new ArrayList<>(state.getTasks());
                    tasks.add(event.getTask());

                    return new TasksState(tasks);
                }
            };

    //----------------------------------------------------------------------------------------------
    // COMPLETE TASK
    //----------------------------------------------------------------------------------------------

    final SyncCommandHandler<TasksState, CompleteTask, TaskCompleted> handleCompleteTask =
            new SyncCommandHandler<TasksState, CompleteTask, TaskCompleted>() {
                @Override
                public TaskCompleted handle(TasksState state, CompleteTask command) {
                    Task task = state.findTaskById(command.getTaskId());

                    if (task == null) {
                        throw new TasksException("Could not find task with id " + command.getTaskId());
                    }

                    return new TaskCompleted(new Task(task.getId(), task.getText(), true));
                }
            };

    final EventHandler<TasksState, TaskCompleted> handleTaskCompleted =
            new EventHandler<TasksState, TaskCompleted>() {
                @Override
                public TasksState handle(TasksState state, TaskCompleted event) {
                    Task completedTask = event.getTask();
                    ArrayList<Task> tasks = new ArrayList<>(state.getTasks());
                    boolean replaced = false;

                    for (Task task : tasks) {
                        if (task.getId().equals(completedTask.getId())) {
                            tasks.remove(task);
                            tasks.add(completedTask);
                            replaced = true;
                            break;
                        }
                    }

                    if (!replaced) {
                        throw new TasksException("Could not find task with id " + completedTask.getId());
                    }

                    return new TasksState(tasks);
                }
            };

    //----------------------------------------------------------------------------------------------
    // REMOVE TASK
    //----------------------------------------------------------------------------------------------

    final SyncCommandHandler<TasksState, RemoveTask, TaskRemoved> handleRemoveTask =
            new SyncCommandHandler<TasksState, RemoveTask, TaskRemoved>() {
                @Override
                public TaskRemoved handle(TasksState state, RemoveTask command) {
                    Task task = state.findTaskById(command.getTaskId());

                    if (task == null) {
                        throw new TasksException("Could not find task with id " + command.getTaskId());
                    }

                    return new TaskRemoved(new Task(task.getId(), task.getText(), task.isCompleted()));
                }
            };

    final EventHandler<TasksState, TaskRemoved> handleTaskRemoved =
            new EventHandler<TasksState, TaskRemoved>() {
                @Override
                public TasksState handle(TasksState state, TaskRemoved event) {
                    Task removedTask = event.getTask();
                    ArrayList<Task> tasks = new ArrayList<>(state.getTasks());
                    boolean removed = false;

                    for (Task task : tasks) {
                        if (task.getId().equals(removedTask.getId())) {
                            tasks.remove(task);
                            removed = true;
                            break;
                        }
                    }

                    if (!removed) {
                        throw new TasksException("Could not find task with id " + removedTask.getId());
                    }

                    return new TasksState(tasks);
                }
            };

}
