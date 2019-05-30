package com.piotrek1543.example.todoapp.presentation.addedittask

import com.piotrek1543.example.todoapp.presentation.base.BaseAction

sealed class AddEditTaskAction : BaseAction {

  data class PopulateTaskAction(val taskId: String) : AddEditTaskAction()

  data class CreateTaskAction(val title: String, val description: String) : AddEditTaskAction()

  data class UpdateTaskAction(
      val taskId: String,
      val title: String,
      val description: String
  ) : AddEditTaskAction()

  object SkipAction : AddEditTaskAction()

}