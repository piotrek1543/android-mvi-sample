package com.piotrek1543.example.todoapp.presentation.tasks

import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.presentation.base.BaseViewState
import com.piotrek1543.example.todoapp.presentation.tasks.TasksFilterType.*

data class TasksViewState(
        val isLoading: Boolean,
        val tasksFilterType: TasksFilterType,
        val tasks: List<Task>,
        val error: Throwable?,
        val uiNotification: UiNotification?
) : BaseViewState {

  enum class UiNotification {
    TASK_COMPLETE,
    TASK_ACTIVATED,
    COMPLETE_TASKS_CLEARED
  }

  companion object {
    fun idle(): TasksViewState {
      return TasksViewState(
          isLoading = false,
          tasksFilterType = ALL_TASKS,
          tasks = emptyList(),
          error = null,
          uiNotification = null
      )
    }
  }

}