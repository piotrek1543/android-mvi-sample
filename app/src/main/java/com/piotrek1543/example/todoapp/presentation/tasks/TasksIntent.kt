package com.piotrek1543.example.todoapp.presentation.tasks

import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.presentation.base.BaseIntent

sealed class TasksIntent : BaseIntent {

    object InitialIntent : TasksIntent()

    data class RefreshIntent(val forceUpdate: Boolean) : TasksIntent()

    data class ActivateTaskIntent(val task: Task) : TasksIntent()

    data class CompleteTaskIntent(val task: Task) : TasksIntent()

    object ClearCompletedTasksIntent : TasksIntent()

    data class ChangeFilterIntent(val filterType: TasksFilterType) : TasksIntent()

}
