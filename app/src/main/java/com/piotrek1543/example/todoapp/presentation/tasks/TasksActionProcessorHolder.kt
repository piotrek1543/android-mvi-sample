package com.piotrek1543.example.todoapp.presentation.tasks

import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import com.piotrek1543.example.todoapp.presentation.base.BaseAction
import com.piotrek1543.example.todoapp.presentation.base.BaseResult
import com.piotrek1543.example.todoapp.presentation.base.BaseViewModel
import com.piotrek1543.example.todoapp.presentation.tasks.TasksAction.*
import com.piotrek1543.example.todoapp.presentation.tasks.TasksResult.*
import com.piotrek1543.example.todoapp.presentation.util.pairWithDelay
import com.piotrek1543.example.todoapp.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * Contains and executes the business logic for all emitted [BaseAction]
 * and returns one unique [Observable] of [BaseResult].
 *
 *
 * This could have been included inside the [BaseViewModel]
 * but was separated to ease maintenance, as the [BaseViewModel] was getting too big.
 */
class TasksActionProcessorHolder(
        private val tasksRepository: TasksRepository,
        private val schedulerProvider: BaseSchedulerProvider
) {

    private val loadTasksProcessor =
            ObservableTransformer<LoadTasksAction, LoadTasksResult> { actions ->
                actions.flatMap { action ->
                    tasksRepository.getTasks(action.forceUpdate)
                            // Transform the Single to an Observable to allow emission of multiple
                            // events down the stream (e.g. the InFlight event)
                            .toObservable()
                            // Wrap returned data into an immutable object
                            .map { tasks -> LoadTasksResult.Success(tasks, action.filterType) }
                            .cast(LoadTasksResult::class.java)
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(LoadTasksResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(LoadTasksResult.InFlight)
                }
            }

    private val activateTaskProcessor =
            ObservableTransformer<ActivateTaskAction, ActivateTaskResult> { actions ->
                actions.flatMap { action ->
                    tasksRepository.activateTask(action.task)
                            .andThen(tasksRepository.getTasks())
                            // Transform the Single to an Observable to allow emission of multiple
                            // events down the stream (e.g. the InFlight event)
                            .toObservable()
                            .flatMap { tasks ->
                                // Emit two events to allow the UI notification to be hidden after
                                // some delay
                                pairWithDelay(
                                        ActivateTaskResult.Success(tasks),
                                        ActivateTaskResult.HideUiNotification)
                            }
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(ActivateTaskResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(ActivateTaskResult.InFlight)
                }
            }

    private val completeTaskProcessor =
            ObservableTransformer<CompleteTaskAction, CompleteTaskResult> { actions ->
                actions.flatMap { action ->
                    tasksRepository.completeTask(action.task)
                            .andThen(tasksRepository.getTasks())
                            // Transform the Single to an Observable to allow emission of multiple
                            // events down the stream (e.g. the InFlight event)
                            .toObservable()
                            .flatMap { tasks ->
                                // Emit two events to allow the UI notification to be hidden after
                                // some delay
                                pairWithDelay(
                                        CompleteTaskResult.Success(tasks),
                                        CompleteTaskResult.HideUiNotification)
                            }
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(CompleteTaskResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(CompleteTaskResult.InFlight)
                }
            }

    private val clearCompletedTasksProcessor =
            ObservableTransformer<ClearCompletedTasksAction, ClearCompletedTasksResult> { actions ->
                actions.flatMap {
                    tasksRepository.clearCompletedTasks()
                            .andThen(tasksRepository.getTasks())
                            // Transform the Single to an Observable to allow emission of multiple
                            // events down the stream (e.g. the InFlight event)
                            .toObservable()
                            .flatMap { tasks ->
                                // Emit two events to allow the UI notification to be hidden after
                                // some delay
                                pairWithDelay(
                                        ClearCompletedTasksResult.Success(tasks),
                                        ClearCompletedTasksResult.HideUiNotification)
                            }
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(ClearCompletedTasksResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(ClearCompletedTasksResult.InFlight)
                }
            }

    /**
     * Splits the [Observable] to match each type of [BaseAction] to
     * its corresponding business logic processor. Each processor takes a defined [BaseAction],
     * returns a defined [BaseResult]
     * The global actionProcessor then merges all [Observable] back to
     * one unique [Observable].
     *
     *
     * The splitting is done using [Observable.publish] which allows almost anything
     * on the passed [Observable] as long as one and only one [Observable] is returned.
     *
     *
     * An security layer is also added for unhandled [BaseAction] to allow early crash
     * at runtime to easy the maintenance.
     */
    internal var actionProcessor =
            ObservableTransformer<TasksAction, TasksResult> { actions ->
                actions.publish { shared ->
                    Observable.merge(
                            // Match LoadTasksAction to loadTasksProcessor
                            shared.ofType(LoadTasksAction::class.java).compose(loadTasksProcessor),
                            // Match ActivateTaskAction to populateTaskProcessor
                            shared.ofType(ActivateTaskAction::class.java)
                                    .compose(activateTaskProcessor),
                            // Match CompleteTaskAction to completeTaskProcessor
                            shared.ofType(CompleteTaskAction::class.java)
                                    .compose(completeTaskProcessor),
                            // Match ClearCompletedTasksAction to clearCompletedTasksProcessor
                            shared.ofType(ClearCompletedTasksAction::class.java)
                                    .compose(clearCompletedTasksProcessor))
                            .mergeWith(
                                    // Error for not implemented actions
                                    shared.filter { v ->
                                        v !is LoadTasksAction
                                                && v !is ActivateTaskAction
                                                && v !is CompleteTaskAction
                                                && v !is ClearCompletedTasksAction
                                    }.flatMap { w ->
                                        Observable.error<TasksResult>(
                                                IllegalArgumentException("Unknown Action type: $w"))
                                    }
                            )
                }
            }
}
