package com.cookingfox.lapasse.samples.todo_vanilla.facade;

import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.facade.LaPasseFacadeDelegate;
import com.cookingfox.lapasse.samples.shared.todo.command.AddTask;
import com.cookingfox.lapasse.samples.shared.todo.command.CompleteTask;
import com.cookingfox.lapasse.samples.shared.todo.command.RemoveTask;
import com.cookingfox.lapasse.samples.shared.todo.entity.Task;
import com.cookingfox.lapasse.samples.shared.todo.event.TaskAdded;
import com.cookingfox.lapasse.samples.shared.todo.event.TaskCompleted;
import com.cookingfox.lapasse.samples.shared.todo.event.TaskRemoved;
import com.cookingfox.lapasse.samples.shared.todo.state.TodoState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * LaPasse {@link Facade} delegate containing the command and event handlers.
 */
public final class TodoFacadeVanilla extends LaPasseFacadeDelegate<TodoState> {

    public TodoFacadeVanilla(Facade<TodoState> facade) {
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

    final SyncCommandHandler<TodoState, AddTask, TaskAdded> handleAddTask =
            new SyncCommandHandler<TodoState, AddTask, TaskAdded>() {
                @Override
                public TaskAdded handle(TodoState state, AddTask command) {
                    Task task = new Task(UUID.randomUUID(), command.getText(), false);

                    return new TaskAdded(task);
                }
            };

    final EventHandler<TodoState, TaskAdded> handleTaskAdded =
            new EventHandler<TodoState, TaskAdded>() {
                @Override
                public TodoState handle(TodoState state, TaskAdded event) {
                    Collection<Task> tasks = new ArrayList<>(state.getTasks());
                    tasks.add(event.getTask());

                    return new TodoState(tasks);
                }
            };

    //----------------------------------------------------------------------------------------------
    // COMPLETE TASK
    //----------------------------------------------------------------------------------------------

    final SyncCommandHandler<TodoState, CompleteTask, TaskCompleted> handleCompleteTask =
            new SyncCommandHandler<TodoState, CompleteTask, TaskCompleted>() {
                @Override
                public TaskCompleted handle(TodoState state, CompleteTask command) {
                    Task task = state.findTaskById(command.getTaskId());

                    if (task == null) {
                        // TODO: 15/06/16 Handle task not found
                        new Exception("Could not find task with id " + command.getTaskId()).printStackTrace();
                        return null;
                    }

                    return new TaskCompleted(new Task(task.getId(), task.getText(), true));
                }
            };

    final EventHandler<TodoState, TaskCompleted> handleTaskCompleted =
            new EventHandler<TodoState, TaskCompleted>() {
                @Override
                public TodoState handle(TodoState state, TaskCompleted event) {
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
                        new Exception("Could not find task with id " + completedTask.getId()).printStackTrace();
                    }

                    return new TodoState(tasks);
                }
            };

    //----------------------------------------------------------------------------------------------
    // REMOVE TASK
    //----------------------------------------------------------------------------------------------

    final SyncCommandHandler<TodoState, RemoveTask, TaskRemoved> handleRemoveTask =
            new SyncCommandHandler<TodoState, RemoveTask, TaskRemoved>() {
                @Override
                public TaskRemoved handle(TodoState state, RemoveTask command) {
                    Task task = state.findTaskById(command.getTaskId());

                    if (task == null) {
                        // TODO: 15/06/16 Handle task not found
                        new Exception("Could not find task with id " + command.getTaskId()).printStackTrace();
                        return null;
                    }

                    return new TaskRemoved(new Task(task.getId(), task.getText(), true));
                }
            };

    final EventHandler<TodoState, TaskRemoved> handleTaskRemoved =
            new EventHandler<TodoState, TaskRemoved>() {
                @Override
                public TodoState handle(TodoState state, TaskRemoved event) {
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
                        // TODO: 15/06/16 Handle task not found
                        new Exception("Could not find task with id " + removedTask.getId()).printStackTrace();
                    }

                    return new TodoState(tasks);
                }
            };

}
