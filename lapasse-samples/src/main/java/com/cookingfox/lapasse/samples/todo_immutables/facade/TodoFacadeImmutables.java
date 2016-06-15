package com.cookingfox.lapasse.samples.todo_immutables.facade;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.facade.LaPasseFacadeDelegate;
import com.cookingfox.lapasse.impl.helper.LaPasse;
import com.cookingfox.lapasse.samples.todo_immutables.command.AddTask;
import com.cookingfox.lapasse.samples.todo_immutables.command.CompleteTask;
import com.cookingfox.lapasse.samples.todo_immutables.command.RemoveTask;
import com.cookingfox.lapasse.samples.todo_immutables.entity.Task;
import com.cookingfox.lapasse.samples.todo_immutables.event.TaskAdded;
import com.cookingfox.lapasse.samples.todo_immutables.event.TaskCompleted;
import com.cookingfox.lapasse.samples.todo_immutables.event.TaskRemoved;
import com.cookingfox.lapasse.samples.todo_immutables.state.TodoState;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * LaPasse {@link Facade} delegate containing the command and event handlers with annotations.
 */
public final class TodoFacadeImmutables extends LaPasseFacadeDelegate<TodoState> {

    public TodoFacadeImmutables(Facade<TodoState> facade) {
        super(facade);

        // required to link annotated code to generated code
        LaPasse.mapHandlers(this);
    }

    //----------------------------------------------------------------------------------------------
    // ADD TASK
    //----------------------------------------------------------------------------------------------

    @HandleCommand
    TaskAdded handle(TodoState state, AddTask command) {
        Task task = new Task.Builder()
                .id(UUID.randomUUID())
                .text(command.getText())
                .isCompleted(false)
                .build();

        return new TaskAdded.Builder().task(task).build();
    }

    @HandleEvent
    TodoState handle(TodoState state, TaskAdded event) {
        Collection<Task> tasks = new ArrayList<>(state.getTasks());
        tasks.add(event.getTask());

        return TodoState.builder()
                .from(state)
                .tasks(tasks)
                .build();
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

        Task completedTask = new Task.Builder()
                .from(task)
                .isCompleted(true)
                .build();

        return new TaskCompleted.Builder()
                .task(completedTask)
                .build();
    }

    @HandleEvent
    TodoState handle(TodoState state, TaskCompleted event) {
        final Task completedTask = event.getTask();

        Collection<Task> tasks = new ArrayList<>(Collections2.filter(state.getTasks(), new Predicate<Task>() {
            @Override
            public boolean apply(Task task) {
                return !task.getId().equals(completedTask.getId());
            }
        }));

        tasks.add(completedTask);

        return TodoState.builder()
                .from(state)
                .tasks(tasks)
                .build();
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

        return new TaskRemoved.Builder()
                .task(task)
                .build();
    }

    @HandleEvent
    TodoState handle(TodoState state, TaskRemoved event) {
        final Task removedTask = event.getTask();

        Collection<Task> tasks = Collections2.filter(state.getTasks(), new Predicate<Task>() {
            @Override
            public boolean apply(Task task) {
                return !task.getId().equals(removedTask.getId());
            }
        });

        return TodoState.builder()
                .from(state)
                .tasks(tasks)
                .build();
    }

}
