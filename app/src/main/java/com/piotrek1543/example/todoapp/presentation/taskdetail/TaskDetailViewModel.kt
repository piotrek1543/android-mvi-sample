package com.piotrek1543.example.todoapp.presentation.taskdetail

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.piotrek1543.example.todoapp.Event
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.data.Result
import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import com.piotrek1543.example.todoapp.ui.util.EspressoIdlingResource
import kotlinx.coroutines.launch

/**
 * Listens to user actions from the list item in ([TasksFragment]) and redirects them to the
 * Fragment's actions listener.
 */
class TaskDetailViewModel(
        private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _task = MutableLiveData<Task>()
    val task = _task

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editTaskCommand = MutableLiveData<Event<Unit>>()
    val editTaskCommand: LiveData<Event<Unit>> = _editTaskCommand

    private val _deleteTaskCommand = MutableLiveData<Event<Unit>>()
    val deleteTaskCommand: LiveData<Event<Unit>> = _deleteTaskCommand

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    // This LiveData depends on another so we can use a transformation.
    val completed: LiveData<Boolean> = Transformations.map(_task) { input: Task? ->
        input?.isCompleted ?: false
    }

    val taskId: String?
        get() = _task.value?.id

    fun deleteTask() = viewModelScope.launch {
        taskId?.let {
            tasksRepository.deleteTask(it)
            _deleteTaskCommand.value = Event(Unit)
        }
    }

    fun editTask() {
        _editTaskCommand.value = Event(Unit)
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val task = _task.value ?: return@launch
        if (completed) {
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    fun start(taskId: String?) {
        _dataLoading.value = true

        // Espresso does not work well with coroutines yet. See
        // https://github.com/Kotlin/kotlinx.coroutines/issues/982
        EspressoIdlingResource.increment() // Set app as busy.

        viewModelScope.launch {
            if (taskId != null) {
                tasksRepository.getTask(taskId, false).let { result ->
                    if (result is Result.Success) {
                        onTaskLoaded(result.data)
                    } else {
                        onDataNotAvailable(result)
                    }
                }
            }
            _dataLoading.value = false
            EspressoIdlingResource.decrement() // Set app as idle.
        }
    }

    private fun setTask(task: Task?) {
        this._task.value = task
        _isDataAvailable.value = task != null
    }

    private fun onTaskLoaded(task: Task) {
        setTask(task)
    }

    private fun onDataNotAvailable(result: Result<Task>) {
        _task.value = null
        _isDataAvailable.value = false
    }

    fun onRefresh() {
        taskId?.let { start(it) }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}
