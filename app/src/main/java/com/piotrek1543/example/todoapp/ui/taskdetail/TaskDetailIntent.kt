package com.piotrek1543.example.todoapp.ui.taskdetail

import com.piotrek1543.example.todoapp.presentation.base.BaseIntent

sealed class TaskDetailIntent : BaseIntent {

    data class InitialIntent(val taskId: String) : TaskDetailIntent()

    data class DeleteTask(val taskId: String) : TaskDetailIntent()

    data class ActivateTaskIntent(val taskId: String) : TaskDetailIntent()

    data class CompleteTaskIntent(val taskId: String) : TaskDetailIntent()

}
