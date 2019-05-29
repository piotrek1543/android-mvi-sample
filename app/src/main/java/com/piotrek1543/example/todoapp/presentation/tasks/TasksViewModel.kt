package com.piotrek1543.example.todoapp.presentation.tasks

import android.arch.lifecycle.ViewModel
import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.presentation.base.BaseViewModel
import com.piotrek1543.example.todoapp.presentation.tasks.TasksAction.*
import com.piotrek1543.example.todoapp.presentation.tasks.TasksFilterType.*
import com.piotrek1543.example.todoapp.presentation.tasks.TasksIntent.*
import com.piotrek1543.example.todoapp.presentation.tasks.TasksResult.*
import com.piotrek1543.example.todoapp.presentation.tasks.TasksResult.CompleteTaskResult.*
import com.piotrek1543.example.todoapp.presentation.tasks.TasksViewState.UiNotification.*
import com.piotrek1543.example.todoapp.presentation.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * Listens to user actions from the UI ([TasksFragment]), retrieves the data and updates the
 * UI as required.
 *
 * @property actionProcessorHolder Contains and executes the business logic of all emitted
 * actions.
 */
class TasksViewModel(
        private val actionProcessorHolder: TasksActionProcessorHolder
) : ViewModel(), BaseViewModel<TasksIntent, TasksViewState> {

    /**
     * Proxy subject used to keep the stream alive even after the UI gets recycled.
     * This is basically used to keep ongoing events and the last cached State alive
     * while the UI disconnects and reconnects on config changes.
     */
    private val intentsSubject: PublishSubject<TasksIntent> = PublishSubject.create()
    
    private val statesObservable: Observable<TasksViewState> = compose()

    /**
     * take only the first ever InitialIntent and all intents of other types
     * to avoid reloading data on config changes
     */
    private val intentFilter: ObservableTransformer<TasksIntent, TasksIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                        shared.ofType(InitialIntent::class.java).take(1),
                        shared.notOfType(InitialIntent::class.java)
                )
            }
        }

    override fun processIntents(intents: Observable<TasksIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<TasksViewState> = statesObservable

    /**
     * Compose all components to create the stream logic
     */
    private fun compose(): Observable<TasksViewState> {
        return intentsSubject
                .compose(intentFilter)
                .map(this::actionFromIntent)
                .compose(actionProcessorHolder.actionProcessor)
                // Cache each state and pass it to the reducer to create a new state from
                // the previous cached one and the latest Result emitted from the action processor.
                // The Scan operator is used here for the caching.
                .scan(TasksViewState.idle(), reducer)
                // When a reducer just emits previousState, there's no reason to call render. In fact,
                // redrawing the UI in cases like this can cause jank (e.g. messing up snackbar animations
                // by showing the same snackbar twice in rapid succession).
                .distinctUntilChanged()
                // Emit the last one event of the stream on subscription
                // Useful when a View rebinds to the ViewModel after rotation.
                .replay(1)
                // Create the stream on creation without waiting for anyone to subscribe
                // This allows the stream to stay alive even when the UI disconnects and
                // match the stream's lifecycle to the ViewModel's one.
                .autoConnect(0)
    }

    /**
     * Translate an [MviIntent] to an [MviAction].
     * Used to decouple the UI and the business logic to allow easy testings and reusability.
     */
    private fun actionFromIntent(intent: TasksIntent): TasksAction {
        return when (intent) {
            is InitialIntent -> LoadTasksAction(true, ALL_TASKS)
            is RefreshIntent -> LoadTasksAction(intent.forceUpdate, null)
            is ActivateTaskIntent -> ActivateTaskAction(intent.task)
            is CompleteTaskIntent -> CompleteTaskAction(intent.task)
            is ClearCompletedTasksIntent -> ClearCompletedTasksAction
            is ChangeFilterIntent -> LoadTasksAction(false, intent.filterType)
        }
    }

    companion object {
        /**
         * The Reducer is where [MviViewState], that the [MviView] will use to
         * render itself, are created.
         * It takes the last cached [MviViewState], the latest [MviResult] and
         * creates a new [MviViewState] by only updating the related fields.
         * This is basically like a big switch statement of all possible types for the [MviResult]
         */
        private val reducer = BiFunction { previousState: TasksViewState, result: TasksResult ->
            when (result) {
                is LoadTasksResult -> when (result) {
                    is LoadTasksResult.Success -> {
                        val filterType = result.filterType ?: previousState.tasksFilterType
                        val tasks = filteredTasks(result.tasks, filterType)
                        previousState.copy(
                                isLoading = false,
                                tasks = tasks,
                                tasksFilterType = filterType
                        )
                    }
                    is LoadTasksResult.Failure -> previousState.copy(isLoading = false, error = result.error)
                    is LoadTasksResult.InFlight -> previousState.copy(isLoading = true)
                }
                is CompleteTaskResult -> when (result) {
                    is Success ->
                        previousState.copy(
                                uiNotification = TASK_COMPLETE,
                                tasks = filteredTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is Failure -> previousState.copy(error = result.error)
                    is InFlight -> previousState
                    is HideUiNotification ->
                        if (previousState.uiNotification == TASK_COMPLETE) {
                            previousState.copy(uiNotification = null)
                        } else {
                            previousState
                        }
                }
                is ActivateTaskResult -> when (result) {
                    is ActivateTaskResult.Success ->
                        previousState.copy(
                                uiNotification = TASK_ACTIVATED,
                                tasks = filteredTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is ActivateTaskResult.Failure -> previousState.copy(error = result.error)
                    is ActivateTaskResult.InFlight -> previousState
                    is ActivateTaskResult.HideUiNotification ->
                        if (previousState.uiNotification == TASK_ACTIVATED) {
                            previousState.copy(uiNotification = null)
                        } else {
                            previousState
                        }
                }
                is ClearCompletedTasksResult -> when (result) {
                    is ClearCompletedTasksResult.Success ->
                        previousState.copy(
                                uiNotification = COMPLETE_TASKS_CLEARED,
                                tasks = filteredTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is ClearCompletedTasksResult.Failure -> previousState.copy(error = result.error)
                    is ClearCompletedTasksResult.InFlight -> previousState
                    is ClearCompletedTasksResult.HideUiNotification ->
                        if (previousState.uiNotification == COMPLETE_TASKS_CLEARED) {
                            previousState.copy(uiNotification = null)
                        } else {
                            previousState
                        }
                }
            }
        }

        private fun filteredTasks(
                tasks: List<Task>,
                filterType: TasksFilterType
        ): List<Task> {
            return when (filterType) {
                ALL_TASKS -> tasks
                ACTIVE_TASKS -> tasks.filter(Task::active)
                COMPLETED_TASKS -> tasks.filter(Task::completed)
            }
        }
    }
}
