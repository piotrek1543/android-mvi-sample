package com.piotrek1543.example.todoapp.presentation.addedittask

import com.piotrek1543.example.todoapp.presentation.base.BaseIntent

sealed class AddEditTaskIntent : BaseIntent {

  data class InitialIntent(val taskId: String?) : AddEditTaskIntent()

  data class SaveTask(
      val taskId: String?,
      val title: String,
      val description: String
  ) : AddEditTaskIntent()

}