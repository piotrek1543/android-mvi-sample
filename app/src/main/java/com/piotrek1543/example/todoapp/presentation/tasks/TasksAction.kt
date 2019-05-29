package com.piotrek1543.example.todoapp.presentation.tasks

import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.presentation.base.BaseAction

sealed class TasksAction : BaseAction {

    data class LoadTasksAction(
            val forceUpdate: Boolean,
            val filterType: TasksFilterType?
    ) : TasksAction()

    data class ActivateTaskAction(val task: Task) : TasksAction()

    data class CompleteTaskAction(val task: Task) : TasksAction()

    object ClearCompletedTasksAction : TasksAction()

}
