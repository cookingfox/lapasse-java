package com.cookingfox.lapasse.samples.todo_annotations.facade;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.facade.LaPasseFacadeDelegate;
import com.cookingfox.lapasse.impl.helper.LaPasse;
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
 * LaPasse {@link Facade} delegate containing the command and event handlers with annotations.
 */
public final class TodoFacadeAnnotations extends LaPasseFacadeDelegate<TodoState> {

    public TodoFacadeAnnotations(Facade<TodoState> facade) {
        super(facade);

        // required to link annotated code to generated code
        LaPasse.mapHandlers(this);
    }

    //----------------------------------------------------------------------------------------------
    // ADD TASK
    //----------------------------------------------------------------------------------------------

    @HandleCommand
    TaskAdded handle(TodoState state, AddTask command) {
        Task task = new Task(UUID.randomUUID(), command.getText(), false);

        return new TaskAdded(task);
    }

    @HandleEvent
    TodoState handle(TodoState state, TaskAdded event) {
        Collection<Task> tasks = new ArrayList<>(state.getTasks());
        tasks.add(event.getTask());

        return new TodoState(tasks);
    }

    //----------------------------------------------------------------------------------------------
    // COMPLETE TASK
    //----------------------------------------------------------------------------------------------

    @HandleCommand
    TaskCompleted handle(TodoState state, CompleteTask command) {
        Task task = state.findTaskById(command.getTaskId());

        if (task == null) {
            // TODO: 15/06/16 Handle task not found
            new Exception("Could not find task with id " + command.getTaskId()).printStackTrace();
            return null;
        }

        return new TaskCompleted(new Task(task.getId(), task.getText(), true));
    }

    @HandleEvent
    TodoState handle(TodoState state, TaskCompleted event) {
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

    //----------------------------------------------------------------------------------------------
    // REMOVE TASK
    //----------------------------------------------------------------------------------------------

    @HandleCommand
    TaskRemoved handle(TodoState state, RemoveTask command) {
        Task task = state.findTaskById(command.getTaskId());

        if (task == null) {
            // TODO: 15/06/16 Handle task not found
            new Exception("Could not find task with id " + command.getTaskId()).printStackTrace();
            return null;
        }

        return new TaskRemoved(new Task(task.getId(), task.getText(), true));
    }

    @HandleEvent
    TodoState handle(TodoState state, TaskRemoved event) {
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

}
