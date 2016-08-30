package com.cookingfox.lapasse.samples.tasks_immutables.facade;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.facade.LaPasseFacadeDelegate;
import com.cookingfox.lapasse.impl.helper.LaPasse;
import com.cookingfox.lapasse.samples.shared.tasks.exception.TasksException;
import com.cookingfox.lapasse.samples.tasks_immutables.command.AddTask;
import com.cookingfox.lapasse.samples.tasks_immutables.command.CompleteTask;
import com.cookingfox.lapasse.samples.tasks_immutables.command.RemoveTask;
import com.cookingfox.lapasse.samples.tasks_immutables.entity.Task;
import com.cookingfox.lapasse.samples.tasks_immutables.event.TaskAdded;
import com.cookingfox.lapasse.samples.tasks_immutables.event.TaskCompleted;
import com.cookingfox.lapasse.samples.tasks_immutables.event.TaskRemoved;
import com.cookingfox.lapasse.samples.tasks_immutables.state.TasksState;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * LaPasse {@link Facade} delegate containing the command and event handlers with annotations.
 */
public final class TasksFacadeImmutables extends LaPasseFacadeDelegate<TasksState> {

    public TasksFacadeImmutables(Facade<TasksState> facade) {
        super(facade);

        // required to link annotated code to generated code
        LaPasse.mapHandlers(this);
    }

    //----------------------------------------------------------------------------------------------
    // ADD TASK
    //----------------------------------------------------------------------------------------------

    @HandleCommand
    TaskAdded handle(TasksState state, AddTask command) {
        Task task = new Task.Builder()
                .id(UUID.randomUUID())
                .text(command.getText())
                .isCompleted(false)
                .build();

        return new TaskAdded.Builder().task(task).build();
    }

    @HandleEvent
    TasksState handle(TasksState state, TaskAdded event) {
        Collection<Task> tasks = new ArrayList<>(state.getTasks());
        tasks.add(event.getTask());

        return TasksState.builder()
                .from(state)
                .tasks(tasks)
                .build();
    }

    //----------------------------------------------------------------------------------------------
    // COMPLETE TASK
    //----------------------------------------------------------------------------------------------

    @HandleCommand
    TaskCompleted handle(TasksState state, CompleteTask command) {
        Task task = state.findTaskById(command.getTaskId());

        if (task == null) {
            throw new TasksException("Could not find task with id " + command.getTaskId());
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
    TasksState handle(TasksState state, TaskCompleted event) {
        final Task completedTask = event.getTask();

        // filter out completed task
        Collection<Task> tasks = new ArrayList<>(Collections2.filter(state.getTasks(), new Predicate<Task>() {
            @Override
            public boolean apply(Task task) {
                return !task.getId().equals(completedTask.getId());
            }
        }));

        // add completed task
        tasks.add(completedTask);

        return TasksState.builder()
                .from(state)
                .tasks(tasks)
                .build();
    }

    //----------------------------------------------------------------------------------------------
    // REMOVE TASK
    //----------------------------------------------------------------------------------------------

    @HandleCommand
    TaskRemoved handle(TasksState state, RemoveTask command) {
        Task task = state.findTaskById(command.getTaskId());

        if (task == null) {
            throw new TasksException("Could not find task with id " + command.getTaskId());
        }

        return new TaskRemoved.Builder()
                .task(task)
                .build();
    }

    @HandleEvent
    TasksState handle(TasksState state, TaskRemoved event) {
        final Task removedTask = event.getTask();

        // filter out removed task
        Collection<Task> tasks = Collections2.filter(state.getTasks(), new Predicate<Task>() {
            @Override
            public boolean apply(Task task) {
                return !task.getId().equals(removedTask.getId());
            }
        });

        return TasksState.builder()
                .from(state)
                .tasks(tasks)
                .build();
    }

}
