package com.piotrek1543.example.todoapp.presentation.addedittask

import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.presentation.base.BaseResult


sealed class AddEditTaskResult : BaseResult {

    sealed class PopulateTaskResult : AddEditTaskResult() {

        data class Success(val task: Task) : PopulateTaskResult()

        data class Failure(val error: Throwable) : PopulateTaskResult()

        object InFlight : PopulateTaskResult()

    }

    sealed class CreateTaskResult : AddEditTaskResult() {

        object Success : CreateTaskResult()

        object Empty : CreateTaskResult()

    }

    object UpdateTaskResult : AddEditTaskResult()

}
