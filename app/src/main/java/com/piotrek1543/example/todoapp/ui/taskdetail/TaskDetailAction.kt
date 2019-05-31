package com.piotrek1543.example.todoapp.ui.taskdetail

import com.piotrek1543.example.todoapp.presentation.base.BaseAction

sealed class TaskDetailAction : BaseAction {

    data class PopulateTaskAction(val taskId: String) : TaskDetailAction()

    data class DeleteTaskAction(val taskId: String) : TaskDetailAction()

    data class ActivateTaskAction(val taskId: String) : TaskDetailAction()

    data class CompleteTaskAction(val taskId: String) : TaskDetailAction()
}