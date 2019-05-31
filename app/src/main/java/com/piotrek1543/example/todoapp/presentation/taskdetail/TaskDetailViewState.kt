package com.piotrek1543.example.todoapp.presentation.taskdetail

import com.piotrek1543.example.todoapp.presentation.base.BaseViewState

data class TaskDetailViewState(
        val title: String,
        val description: String,
        val active: Boolean,
        val loading: Boolean,
        val error: Throwable?,
        val uiNotification: UiNotification?
) : BaseViewState {
    enum class UiNotification {
        TASK_COMPLETE,
        TASK_ACTIVATED,
        TASK_DELETED
    }

    companion object {

        fun idle(): TaskDetailViewState {
            return TaskDetailViewState(
                    title = "",
                    description = "",
                    active = false,
                    loading = false,
                    error = null,
                    uiNotification = null
            )
        }
    }
}
